package com.eyespynature.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.eyespynature.client.service.FileServiceAsync;
import com.eyespynature.shared.FileDescription;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class FileSelectionPanel extends Composite {

	interface MyUiBinder extends UiBinder<Widget, FileSelectionPanel> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private final FileServiceAsync fileService = FileServiceAsync.Util
			.getInstance();

	final private static Logger logger = Logger
			.getLogger(FileSelectionPanel.class.getName());

	@UiField
	Hidden dirInForm;

	@UiField
	Label dir;

	@UiField
	Grid grid;

	@UiField
	Button clearImages;

	@UiField
	Grid selected;

	@UiField
	FormPanel form;

	@UiField
	FileUpload fileupload;

	@UiField
	TextBox filename;

	@UiField
	HTML status;

	@UiField
	TextBox dirname;

	@UiField
	VerticalPanel writeBlock;

	@UiField
	HorizontalPanel readBlock;

	private String mode;

	public String getMode() {
		return mode;
	}

	private List<String> files = new ArrayList<String>();

	private Set<String> imageExtentions = new HashSet<String>(Arrays.asList(
			"jpg", "png"));

	private Button button;

	public void setMode(String mode) {
		this.mode = mode;
		if (mode == "read") {
			writeBlock.setVisible(false);
		} else {
			readBlock.setVisible(false);
		}
	}

	@UiHandler("submit")
	public void submit(ClickEvent event) {
		dirInForm.setValue(dir.getText());
		form.submit();
		filename.setValue("");
	}

	@UiHandler("clearImages")
	public void clearImages(ClickEvent event) {
		for (int i = 0; i < grid.getRowCount(); i++) {
			if (grid.getWidget(i, 2) != null) {
				CheckBox cb = (CheckBox) grid.getWidget(i, 2);
				if (cb.getValue()) {
					cb.setValue(false);
				}
			}
		}
		files.clear();
		enable();
		selected.clear();
		clearImages.setVisible(false);
		
	}

	private void enable() {
		if (button != null) {
			button.setEnabled(true);
		}
	}

	@UiHandler("submitNewDir")
	public void submitNewDir(ClickEvent event) {
		status.setText("Refreshing ...");
		fileService.mkdir(dir.getText() + "/" + dirname.getText(),
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						status.setHTML(caught.toString());
						dirname.setText("");
					}

					@Override
					public void onSuccess(Void result) {
						dirname.setText("");
						refresh();
					}
				});
	}

	@UiHandler("fileupload")
	public void nameChanged(ChangeEvent event) {
		String fname = fileupload.getFilename();
		logger.fine(fname);
		if (fname != null) {
			int last = fname.lastIndexOf('\\');
			logger.fine("last " + last);
			if (last >= 0) {
				filename.setText(fname.substring(last + 1));
			}
		}
	}

	public FileSelectionPanel() {
		initWidget(uiBinder.createAndBindUi(this));

		form.setAction("upload");

		// Because we're going to add a FileUpload widget, we'll need to set the
		// form to use the POST method, and multipart MIME encoding.
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);

		form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				status.setHTML(event.getResults());
				refresh();
			}
		});

		grid.resizeColumns(3);
		selected.resizeRows(1);
		clearImages.setVisible(false);
		refresh();
	}

	private void refresh() {
		status.setText("Refreshing ...");
		fileService.getFiles(dir.getText(),
				new AsyncCallback<List<FileDescription>>() {

					@Override
					public void onFailure(Throwable caught) {
						status.setHTML(caught.toString());
					}

					@Override
					public void onSuccess(List<FileDescription> result) {
						grid.clear();
						int n = 0;
						if (dir.getText().isEmpty()) {
							grid.resizeRows(result.size());
						} else {
							grid.resizeRows(result.size() + 1);
							grid.setText(n, 0, "..");
							Button b = new Button("Up");
							b.addClickHandler(new ClickHandler() {
								@Override
								public void onClick(ClickEvent event) {
									String text = dir.getText();
									int last = text.lastIndexOf('/');
									dir.setText(text.substring(0, last));
									refresh();
								}
							});
							grid.setWidget(n, 1, b);
							n++;
						}
						for (FileDescription fd : result) {
							final String name = fd.getName();
							grid.setText(n, 0, name);
							if (fd.isDirectory()) {
								Button b = new Button("Open");
								b.addClickHandler(new ClickHandler() {
									@Override
									public void onClick(ClickEvent event) {
										dir.setText((dir.getText() + "/" + name));
										refresh();
									}
								});
								grid.setWidget(n, 1, b);
							} else {
								int p = name.lastIndexOf('.');
								if (p >= 0) {
									String ext = name.substring(p + 1)
											.toLowerCase();
									logger.fine("Ext is " + ext + " in "
											+ imageExtentions);
									if (imageExtentions.contains(ext)) {
										logger.fine("Yes" + "download?name="
												+ dir.getText() + "/" + name);
										Image image = new Image(
												"download?name="
														+ dir.getText() + "/"
														+ name
														+ "&width=64&height=64");
										grid.setWidget(n, 1, image);
									}
								}
							}
							if (mode == "write") {
								Button b = new Button("Delete");
								b.addClickHandler(new ClickHandler() {
									@Override
									public void onClick(ClickEvent event) {
										fileService.deleteFile(dir.getText()
												+ "/" + name,
												new AsyncCallback<Void>() {
													@Override
													public void onFailure(
															Throwable caught) {
														status.setHTML(caught
																.toString());
													}

													@Override
													public void onSuccess(
															Void result) {
														refresh();
													}
												});
									}
								});
								grid.setWidget(n, 2, b);
							} else if (!fd.isDirectory()) {
								final CheckBox b = new CheckBox();
								String fileName = dir.getText() + "/" + name;
								b.setValue(files.contains(fileName));
								b.addClickHandler(new ClickHandler() {
									@Override
									public void onClick(ClickEvent event) {
										if (b.getValue()) {
											files.add(dir.getText() + "/"
													+ name);
										} else {
											files.remove(dir.getText() + "/"
													+ name);
										}

										fillImageList();
										enable();
									}

								});
								grid.setWidget(n, 2, b);
							}

							n++;
						}
						status.setText("");
					}
				});

	}

	private void fillImageList() {
		int n = files.size();
		selected.resizeColumns(n);
		clearImages.setVisible(n != 0);
		int m = 0;
		for (String file : files) {
			selected.setWidget(0, m, new Image("download?name=" + file
					+ "&width=100&height=100"));
			m++;
		}
	}

	public List<String> getNames() {
		return this.files;
	}

	public void setNames(List<String> files) {
		this.files.clear();
		this.files.addAll(files);
		enable();
		dir.setText("");
		logger.fine("Set file list to " + files);
		fillImageList();
		refresh();
	}

	public void setChanges(Button button) {
		this.button = button;
	}
}
