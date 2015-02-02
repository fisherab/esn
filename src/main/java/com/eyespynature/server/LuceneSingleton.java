package com.eyespynature.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.eyespynature.client.service.ProductService;
import com.eyespynature.client.service.ProductService.SortType;
import com.eyespynature.shared.InternalException;
import com.eyespynature.shared.ProductTypeTransferObject;

public class LuceneSingleton {

	private static LuceneSingleton instance;
	final static Logger logger = Logger.getLogger(LuceneSingleton.class);
	private static int users;

	public synchronized static LuceneSingleton getInstance() {
		if (instance == null) {
			instance = new LuceneSingleton();
		} else if (instance.directory == null) {
			instance.refresh();
			logger.debug("Refreshed LuceneSingleton");
		}
		users++;
		logger.debug("LuceneSingleton has " + users + " users.");
		return instance;
	}

	private void refresh() {
		try {
			directory = FSDirectory.open(new File("../data/esn/index"));
			logger.debug("Opened FSDirectory with lockid " + directory.getLockID());
			Analyzer analyzer = new ESNAnalyzer(Version.LUCENE_40);
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, analyzer);
			iwriter = new IndexWriter(directory, config);
			String[] files = directory.listAll();
			if (files.length == 1 && files[0].equals("write.lock")) {
				logger.debug("Directory only has the write.lock file so commit and reopen");
				iwriter.commit();
				iwriter.close();
				iwriter = new IndexWriter(directory, config);
			}
			ireader = DirectoryReader.open(directory);
			isearcher = new IndexSearcher(ireader);
			parser = new QueryParser(Version.LUCENE_40, "body", analyzer);
		} catch (Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			logger.fatal(errors.toString());
			if (directory != null) {
				try {
					String lockId = directory.getLockID();
					directory.clearLock(lockId);
					logger.warn("Cleared lock " + lockId);
				} catch (IOException e1) {
					// Ignore
				}
			}
			throw new IllegalStateException(errors.toString());
		}
	}

	private IndexWriter iwriter;
	private QueryParser parser;
	private FSDirectory directory;
	private IndexSearcher isearcher;
	private DirectoryReader ireader;

	private LuceneSingleton() {
		refresh();
		logger.debug("Created LuceneSingleton");
	}

	private Document buildDoc(Long key, ProductTypeTransferObject ptto) {
		String text = "";
		for (String item : ptto.getMenu()) {
			text += item + " ";
		}
		text += ptto.getName() + " " + ptto.getShortD() + " " + ptto.getLongD() + " "
				+ ptto.getSpec() + " " + ptto.getNotes() + " " + ptto.getTags();

		Document doc = new Document();
		doc.add(new StringField("id", key.toString(), Store.YES));
		doc.add(new LongField("price", ptto.getPrice(), Store.NO));
		doc.add(new TextField("body", text, Store.NO));
		return doc;
	}

	public void addDocument(Long key, ProductTypeTransferObject ptto) throws InternalException {
		Document doc = buildDoc(key, ptto);
		try {
			logger.debug("Added " + key + " to index for " + ptto.getId());
			iwriter.addDocument(doc);
		} catch (IOException e) {
			throw new InternalException(e.getMessage());
		}
	}

	public void commit() throws InternalException {
		try {
			iwriter.commit();
			logger.debug("Now have " + iwriter.numDocs() + " products indexed");
		} catch (IOException e) {
			throw new InternalException(e.getMessage());
		}

	}

	public void updateDocument(Long key, ProductTypeTransferObject ptto) throws InternalException {
		Document doc = buildDoc(key, ptto);
		try {
			iwriter.updateDocument(new Term("id", key.toString()), doc);
		} catch (IOException e) {
			throw new InternalException(e.getMessage());
		}
	}

	public void reindex() throws InternalException {
		try {
			iwriter.deleteAll();
		} catch (IOException e) {
			throw new InternalException(e.getMessage());
		}
	}

	public synchronized void close() {
		users--;
		if (users == 0) {
			try {
				logger.debug("Closing IndexWriter for directory lockid " + directory.getLockID());
				iwriter.commit();
				iwriter.close();
				iwriter = null;
				logger.debug("IndexWriter closed for directory lockid " + directory.getLockID());
				ireader.close();
				logger.debug("IndexReader closed for directory lockid " + directory.getLockID());
				directory.close();
				directory = null;
				logger.debug("Directory closed");
			} catch (Exception e) {
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				logger.fatal(errors.toString());
			}
		} else {
			logger.debug("LuceneSingleton still has " + users + " users after close.");
		}
	}

	public List<Long> search(String queryString, int offset, int count, SortType sortType)
			throws InternalException {
		try {
			List<Long> results = new ArrayList<Long>();
			ScoreDoc[] hits = null;
			Query query = parser.parse(queryString);
			if (sortType == ProductService.SortType.RELEVANCE) {
				hits = isearcher.search(query, offset + count).scoreDocs;
			} else {
				Sort sort = null;
				if (sortType == ProductService.SortType.PRICEL2H) {
					sort = new Sort(new SortField("price", SortField.Type.LONG, false),
							SortField.FIELD_DOC);
				} else if (sortType == ProductService.SortType.PRICEH2L) {
					sort = new Sort(new SortField("price", SortField.Type.LONG, true),
							SortField.FIELD_DOC);
				} else {
					throw new InternalException("Unexpected value for SortType");
				}
				hits = isearcher.search(query, offset + count, sort).scoreDocs;
			}
			for (int i = offset; i < hits.length; i++) {
				results.add(Long.parseLong(isearcher.doc(hits[i].doc).get("id")));
				logger.debug("Retrieved " + isearcher.doc(hits[i].doc));
			}
			return results;
		} catch (Exception e) {
			throw new InternalException(e.getMessage());
		}
	}

	public int getCount(String queryString) throws InternalException {
		try {
			TotalHitCountCollector counter = new TotalHitCountCollector();
			isearcher.search(parser.parse(queryString), counter);
			return counter.getTotalHits();
		} catch (Exception e) {
			throw new InternalException(e.getMessage());
		}
	}
}
