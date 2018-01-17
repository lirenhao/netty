package org.ct.concurrent;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class CTFuture<T> {
	private ExecutorService es = Executors.newFixedThreadPool(16);
	private AtomicReference<Object> result = new AtomicReference<Object>();
	private ConcurrentLinkedQueue<Runnable> callbacks = new ConcurrentLinkedQueue<>();
	private ConcurrentLinkedQueue<Runnable> errCallbacks = new ConcurrentLinkedQueue<>();

	public boolean complete(T value) {
		boolean changed = false;

		if (value == null) {
			changed = result.compareAndSet(null, NIL);
		} else {
			changed = result.compareAndSet(null, value);
		}

		if(changed) {
			run(callbacks);
		}
		
		return changed;
	}

	public boolean completeThrowable(Throwable ex) {
		boolean changed = result.compareAndSet(null, new AltResult(ex));

		if(changed)
			run(errCallbacks);
		
		return changed;
	}

	@SuppressWarnings("unchecked")
	public <E> CTFuture<E> then(Function<T, E> func) {
		final CTFuture<E> f = new CTFuture<E>();

		final Runnable worker = () -> {
			try {
				if (result.get() == NIL || !(result.get() instanceof AltResult)) {
					f.complete(func.apply(result.get() == NIL ? null : (T) result.get()));
				} else {
					f.completeThrowable(((AltResult) result.get()).ex);
				}
			} catch (Throwable ex) {
				f.completeThrowable(ex);
			}
		};
		if (result.get() == null) {
			callbacks.offer(worker);
		} else {
			worker.run();
		}
		return f;
	}

	public <E> CTFuture<E> catchThrow(Function<Throwable, E> func) {
		final CTFuture<E> f = new CTFuture<E>();

		final Runnable worker = () -> {
			try {
				if (result.get() != NIL && (result.get() instanceof AltResult)) {
					f.complete(func.apply(((AltResult)result.get()).ex));
				}
			} catch (Throwable ex) {
				f.completeThrowable(ex);
			}
		};
		if (result.get() == null) {
			errCallbacks.offer(worker);
		} else {
			worker.run();
		}
		return f;
	}

	@SuppressWarnings("unchecked")
	public <E> CTFuture<E> faltMap(Function<T, CTFuture<E>> func) {
		final CTFuture<E> f = new CTFuture<E>();

		final Runnable worker = () -> {
			try {
				if (result.get() == NIL || !(result.get() instanceof AltResult)) {
					func.apply(result.get() == NIL ? null : (T) result.get()).then(r -> f.complete(r));
				} else {
					f.completeThrowable(((AltResult) result.get()).ex);
				}
			} catch (Throwable ex) {
				f.completeThrowable(ex);
			}
		};

		if (result.get() == null) {
			callbacks.offer(worker);
		} else {
			worker.run();
		}
		return f;
	}
	
	private void run(ConcurrentLinkedQueue<Runnable> callbacks) {
		Runnable r;
		while((r = callbacks.poll()) != null) {
			es.execute(r);
		}
	}
	/* ------------- Encoding and decoding outcomes -------------- */

	static final class AltResult { // See above
		final Throwable ex; // null only for NIL

		AltResult(Throwable x) {
			this.ex = x;
		}
	}

	/** The encoding of the null value. */
	static final AltResult NIL = new AltResult(null);
}
