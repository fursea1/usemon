/*
 *  Copyright (C) 2003  Jens Kanschik,
 * 	mail : jensKanschik@users.sourceforge.net
 *
 *  Part of <hypergraph>, an open source project at sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package hypergraph.visualnet;

/**
 * @author Jens Kanschik
 * 
 * To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class IteratingGraphLayout extends AbstractGraphLayout {

	long totalTime;
	long numberOfIter;

	private IterationThread thread = new IterationThread();

	protected IterationThread getThread() {
		return thread;
	}

	protected abstract void iteration(IterationThread thread);

	public void invalidate() {
		super.invalidate();
		thread.startIterating(); // if the layout is not valid, don't continue to iterate based on old data.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see visualnet.GraphLayout#layout()
	 */
	public void layout() {
		if (thread != null && !thread.isRunning()) {
			thread.start();
			thread.startIterating();
		}
	}

	public class IterationThread extends Thread {
		private boolean iterating = false;
		private boolean running = false;

		public void startIterating() {
			iterating = true;
		}

		public void stopIterating() {
			iterating = false;
		}

		public boolean isIterating() {
			return iterating;
		}

		public void stopThread() {
			running = false;
		}

		public boolean isRunning() {
			return running;
		}

		public void run() {
			running = true;
			while (running) {
				if (isIterating()) {
					numberOfIter++;
					long ms = System.currentTimeMillis();
					synchronized (getGraph()) {
						iteration(this);
					}
					ms = System.currentTimeMillis() - ms;
					totalTime += ms;
					// System.out.println(ms + " " + totalTime/numberOfIter + " " + graph.getNodes().size());
				}
				try {
					sleep(10);
				} catch (Exception e) {
				}
			}
		}

	}
}
