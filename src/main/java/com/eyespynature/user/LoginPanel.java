package com.eyespynature.user;

import java.util.Date;

import com.eyespynature.client.TheEventBus;
import com.eyespynature.client.service.LoginServiceAsync;
import com.eyespynature.shared.LoginResponse;
import com.eyespynature.user.events.LoginEvent;
import com.eyespynature.user.events.LoginEventHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class LoginPanel extends Composite {

	interface MyUiBinder extends UiBinder<Widget, LoginPanel> {
	}

	private String sessionid;

	private String[] privs = new String[] { "ALL", "PRODUCTS", "STOCK", "REPORTS" };

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private static final LoginServiceAsync loginService = LoginServiceAsync.Util.getInstance();

	@UiField
	VerticalPanel login;

	@UiField
	VerticalPanel change;

	@UiField
	VerticalPanel register;

	@UiField
	VerticalPanel main;

	@UiField
	TextBox registerEmail;

	@UiField
	ListBox registerPriv;

	@UiField
	PasswordTextBox registerPwd;

	@UiField
	HTML user;

	@UiField
	TextBox loginEmail;

	@UiField
	PasswordTextBox loginPwd;

	@UiField
	TextBox chPwdEmail;

	@UiField
	PasswordTextBox chPwdPwd;

	@UiField
	PasswordTextBox chPwdPwdAgain;

	@UiField
	TextBox unregisterEmail;

	@UiField
	TextBox chEmailEmail;

	@UiField
	TextBox chEmailNewEmail;

	@UiField
	TextBox chPrivEmail;

	@UiField
	ListBox chPrivPriv;

	@UiField
	HTML message;

	private static final EventBus bus = TheEventBus.getInstance();

	@UiHandler("registerButton")
	void handleRegister(ClickEvent e) {
		String priv = registerPriv.getItemText(registerPriv.getSelectedIndex());
		final String email = registerEmail.getText();
		final String pwd = registerPwd.getText();
		if (email.isEmpty() || pwd.isEmpty()) {
			Window.alert("Failed: email and password must be set");
			return;
		}

		loginService.register(sessionid, email, pwd, priv, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed: " + caught);
			}

			@Override
			public void onSuccess(Void result) {
				message("New user " + email + " registered");
			}
		});

	}

	@UiHandler("loginButton")
	void handleLogin(ClickEvent e) {
		final String email = loginEmail.getText();
		final String pwd = loginPwd.getText();
		if (email.isEmpty() || pwd.isEmpty()) {
			Window.alert("Failed: email and password must be set");
			return;
		}
		loginService.login(email, pwd, new AsyncCallback<LoginResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed: " + caught);
			}

			@Override
			public void onSuccess(LoginResponse loginResponse) {
				LoginPanel.this.sessionid = loginResponse.getSessionid();
				LoginPanel.this.user.setHTML("Logged in as <b>" + email + "</b>");
				chPrivEmail.setText(email);
				chPwdEmail.setText(email);
				chEmailEmail.setText(email);
				bus.fireEvent(new LoginEvent(email, loginResponse.getSessionid(), loginResponse
						.getPriv()));
				message("Login succesful");
			}
		});
	}

	@UiHandler("chPwdButton")
	void handleChPwd(ClickEvent e) {
		final String email = chPwdEmail.getText();
		final String pwd = chPwdPwd.getText();
		if (sessionid == null) {
			Window.alert("Failed: you must be logged in");
			return;
		}
		if (email.isEmpty() || pwd.isEmpty()) {
			Window.alert("Failed: email and password must be set");
			return;
		}
		if (!pwd.equals(chPwdPwdAgain.getText())) {
			Window.alert("Failed: the two supplied passwords must be the same");
			return;
		}
		loginService.chPwd(sessionid, email, pwd, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed: " + caught);
			}

			@Override
			public void onSuccess(Void result) {
				message("Password changed for " + email);
			}
		});
	}

	private void message(String msg) {
		Date d = new Date();
		message.setHTML(d + ": " + msg);
	}

	@UiHandler("logoutButton")
	void handleLogout(ClickEvent e) {
		if (sessionid == null) {
			Window.alert("Failed: you must be logged in");
			return;
		}
		loginService.logout(sessionid, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed: " + caught);
			}

			@Override
			public void onSuccess(Void result) {
				LoginPanel.this.user.setText("");
				chPrivEmail.setText("");
				chPwdEmail.setText("");
				chEmailEmail.setText("");
				bus.fireEvent(new LoginEvent());
				message("Logout succesful");
			}
		});
	}

	@UiHandler("unregisterButton")
	void handleUnregister(ClickEvent e) {
		final String email = unregisterEmail.getText();
		if (sessionid == null) {
			Window.alert("Failed: you must be logged in");
			return;
		}
		if (email.isEmpty()) {
			Window.alert("Failed: email must be set");
			return;
		}
		loginService.unregister(sessionid, email, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed: " + caught);
			}

			@Override
			public void onSuccess(Void result) {
				message("Unregister of " + email + " was succesful");
			}
		});
	}

	@UiHandler("chEmailButton")
	void handleChEmail(ClickEvent e) {
		final String email = chEmailEmail.getText();
		final String newEmail = chEmailNewEmail.getText();
		if (sessionid == null) {
			Window.alert("Failed: you must be logged in");
			return;
		}
		if (email.isEmpty() || newEmail.isEmpty()) {
			Window.alert("Failed: email and new email must be set");
			return;
		}
		loginService.chEmail(sessionid, email, newEmail, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed: " + caught);
			}

			@Override
			public void onSuccess(Void result) {
				message("Email changed for " + email + " to " + newEmail);
			}
		});
	}

	@UiHandler("chPrivButton")
	void handleChPriv(ClickEvent e) {
		final String priv = chPrivPriv.getItemText(chPrivPriv.getSelectedIndex());
		final String email = chPrivEmail.getText();
		if (sessionid == null) {
			Window.alert("Failed: you must be logged in");
			return;
		}
		if (email.isEmpty()) {
			Window.alert("Failed: email must be set");
			return;
		}
		loginService.chPriv(sessionid, email, priv, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed: " + caught);
			}

			@Override
			public void onSuccess(Void result) {
				message("Privileges changed for " + email + " to " + priv);
			}
		});
	}

	public LoginPanel() {

		initWidget(uiBinder.createAndBindUi(this));
		for (String s : privs) {
			registerPriv.addItem(s);
			chPrivPriv.addItem(s);
		}
		change.setVisible(false);
		register.setVisible(false);

		bus.addHandler(LoginEvent.TYPE, new LoginEventHandler() {

			@Override
			public void onEvent(LoginEvent loginEvent) {
				String priv = loginEvent.getPriv();
				if (priv == null) {
					login.setVisible(true);
					change.setVisible(false);
					register.setVisible(false);
				} else if (priv.equals("ALL")) {
					login.setVisible(true);
					change.setVisible(true);
					register.setVisible(true);
				} else {
					login.setVisible(true);
					change.setVisible(true);
					register.setVisible(false);
				}
			}
		});
	}
}
