package org.ct;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public class ForkJoinTaskTest {
	public static void main(String[] args) throws Throwable {
		final List<MyTask> list = new ArrayList<MyTask>();
		ForkJoinPool pool = new ForkJoinPool();
		for (int i = 0; i < 80; i++) {
			MyTask t = new MyTask();
			list.add(t);
			pool.submit(t);
		}

		for (int i = 0; i < 80; i++) {
			final int j = i;
			pool.submit(new ForkJoinTask<String>() {
				private String v;

				@Override
				public String getRawResult() {
					return v;
				}

				@Override
				protected void setRawResult(String value) {
					v = value;
				}

				@Override
				protected boolean exec() {
					list.get(j).join();
					while (true) {
					}
				}

			});
		}

		while (true) {
			int i = System.in.read();
			if (i == 10) {
				System.out.println(pool.getQueuedSubmissionCount());
				System.out.println(pool.getPoolSize());
				System.out.println(pool.getParallelism());
				System.out.println(pool.getActiveThreadCount());
				System.out.println(pool.getQueuedTaskCount());
				System.out.println(pool.getStealCount());
				list.remove(0).complete("123");
			}
		}
	}

}
