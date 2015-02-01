package com.eyespynature.server;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

@WebServlet(name = "download", urlPatterns = { "/download" })
@SuppressWarnings("serial")
public class DownloadServlet extends HttpServlet {

	@Override
	public void init() {
		try {
			Properties.check();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		for (File f : Properties.getCacheBaseDir().listFiles()) {
			f.delete();
		}
		logger.debug("Cache cleared");
	}

	final static Logger logger = Logger.getLogger(DownloadServlet.class);
	private static final int MAX_ENTRIES = 100;

	Map<String, Integer> map = new LinkedHashMap<String, Integer>(MAX_ENTRIES, (float) 0.7, true) {

		/* Only delete oldest entry if not in use */
		@Override
		protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
			if (eldest.getValue() == 0) {
				boolean remove = size() > MAX_ENTRIES;
				if (remove) {
					logger.debug("Removing old map entry for " + eldest);
					File f = new File(Properties.getCacheBaseDir(), eldest.getKey());
					f.delete();
				}
				return remove;
			} else {
				return false;
			}
		}

	};

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException,
			ServletException {

		try {
			String name = req.getParameter("name");
			if (name == null) {
				throw new IOException("Requested file was null");
			}

			String reqWidthString = req.getParameter("width");
			String reqHeightString = req.getParameter("height");

			if (name.endsWith(".jpg")) {
				response.setContentType("application/jpeg");
			}
			String[] bits = name.split("/");
			response.setHeader("Content-disposition", "inline; filename=" + bits[bits.length - 1]);
			ServletOutputStream sos = response.getOutputStream();
			File f = new File(Properties.getFileBaseDir(), name);
			if (!f.exists()) {
				throw new IOException("Requested file " + name + " does not exist");
			}
			int p = name.lastIndexOf('.');
			if (p < 0) {
				throw new IOException("Requested file " + name + " has no extension");
			}

			if (reqWidthString == null && reqHeightString == null) {
				output(f, sos);
				return;
			}

			int reqWidth = reqWidthString == null ? 0 : Integer.parseInt(reqWidthString);
			int reqHeight = reqHeightString == null ? 0 : Integer.parseInt(reqHeightString);

			String key = name.replace("/", ",") + " " + reqWidth + "x" + reqHeight;
			Integer entry = null;
			boolean needsCacheEntry = false;
			while (true) {
				synchronized (map) {
					while (map.size() > MAX_ENTRIES) {
						String keyToGo = null;
						for (Entry<String, Integer> e : map.entrySet()) {
							if (e.getValue() == 0) {
								keyToGo = e.getKey();
								break;
							}
						}
						map.remove(keyToGo);
						logger.debug("Remove entry for " + keyToGo + " as map has become too big.");
					}

					entry = map.get(key);
					if (entry != null) {
						if (entry >= 0) {
							map.put(key, entry + 1); // Increase current use count
							logger.debug("Reuse entry from cache for " + key);
							break;
						}
					} else {
						map.put(key, -1); // New entry added
						needsCacheEntry = true;
						logger.debug("Will add a new entry for " + key);
						break;
					}
				}
				try {
					logger.debug("Sleep for a second while someone else prepares the cache entry for "
							+ key);
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// Carry on
				}

			}

			if (needsCacheEntry) {

				BufferedImage image = ImageIO.read(f);

				float scale = 0;
				if (reqWidthString != null & reqHeightString != null) {
					scale = Math.max(image.getHeight() / (float) reqHeight, image.getWidth()
							/ (float) reqWidth);
				} else if (reqWidthString != null) {
					scale = image.getWidth() / (float) reqWidth;
				} else {
					scale = image.getHeight() / (float) reqHeight;
				}

				try {
					reqHeight = (int) (image.getHeight() / scale);
					reqWidth = (int) (image.getWidth() / scale);
					logger.debug("Scaling " + name + " down by " + scale + " to " + reqWidth + "x"
							+ reqHeight);

					BufferedImage newImage = new BufferedImage(reqWidth, reqHeight, image.getType());
					Graphics2D g = newImage.createGraphics();
					g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
							RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
					g.drawImage(image, 0, 0, reqWidth, reqHeight, null);

					ImageOutputStream ios = ImageIO.createImageOutputStream(new File(Properties
							.getCacheBaseDir(), key));
					ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
					writer.setOutput(ios);
					writer.write(newImage);
					g.dispose();

					synchronized (map) {
						map.put(key, 1);
					}
					logger.debug("Scaling of " + name + " complete");
				} catch (Exception e) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					e.printStackTrace(new PrintStream(baos));
					String msg = e.getClass() + " reports " + e.getMessage();
					logger.debug(msg + baos.toString());
				}
			}

			f = new File(Properties.getCacheBaseDir(), key);
			output(f, sos);

			synchronized (map) {
				entry = map.get(key);
				if (entry != null) {
					map.put(key, entry - 1);
				}
			}
		} catch (Exception e) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			e.printStackTrace(new PrintStream(baos));
			String msg = e.getClass() + " reports " + e.getMessage();
			logger.debug(msg + baos.toString());
			throw new ServletException(msg);

		}

	}

	private void output(File f, ServletOutputStream sos) throws IOException {
		FileInputStream fis = new FileInputStream(f);
		byte[] buff = new byte[1024];
		int n;
		while ((n = fis.read(buff)) >= 0) {
			sos.write(buff, 0, n);
		}
		fis.close();
	}
}
