package com.eyespynature.server.bean;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import com.eyespynature.server.BCrypt;
import com.eyespynature.server.entity.Session;
import com.eyespynature.server.entity.User;
import com.eyespynature.server.entity.User.Priv;
import com.eyespynature.shared.AuthException;
import com.eyespynature.shared.InternalException;
import com.eyespynature.shared.LoginResponse;

@Stateless
public class LoginServiceBean {

	@PersistenceContext(unitName = "esn")
	private EntityManager em;

	private final static Logger logger = Logger.getLogger(LoginServiceBean.class);

	public LoginResponse login(String email, String pwd) throws InternalException, AuthException {

		Priv priv = null;
		String sessionid = null;

		User u = em.find(User.class, email);
		if (u != null && BCrypt.checkpw(pwd, u.getPwd())) {
			priv = u.getPriv();
		}

		if (priv == null) {
			throw new AuthException("email and password do not match");
		}

		Session session = new Session(email, priv);
		sessionid = session.getKey();
		em.persist(session);

		logger.debug(email + " logged in with sessionid " + sessionid);
		return new LoginResponse(sessionid, priv.name());
	}

	public void logout(String sessionid) throws InternalException {

		Session u = em.find(Session.class, sessionid);
		if (u != null) {
			em.remove(u);
			logger.debug("session removed");
		} else {
			logger.debug("session not found");
		}

	}

	public void register(String sessionid, String email, String pwd, String priv)
			throws AuthException, InternalException {

		Priv uPriv = Priv.valueOf(priv);
		if (sessionid == null) {

			int count;

			count = (Integer) em.createNamedQuery("User.Count").getSingleResult();

			if (count != 0) {
				throw new AuthException("You are not logged in");
			}
			uPriv = Priv.ALL;
		} else {

			boolean fail = false;

			Session s = em.find(Session.class, sessionid);
			if (s == null || s.getPriv() != Priv.ALL) {
				fail = true;
			}

			if (fail) {
				throw new AuthException("You do not have the privileges");
			}

			User u = em.find(User.class, email);
			if (u != null) {
				fail = true;
			}

			if (fail) {
				throw new AuthException(email + " is already registered");
			}
		}
		String hashed = BCrypt.hashpw(pwd, BCrypt.gensalt());

		em.persist(new User(email, hashed, uPriv));

	}

	public void chPwd(String sessionid, String email, String newPassword) throws InternalException,
			AuthException {

		boolean fail = false;

		Session s = em.find(Session.class, sessionid);
		if (s == null) {
			fail = true;
		} else if (s.getPriv() != Priv.ALL && !s.getEmail().equals(email)) {
			fail = true;
		}

		if (fail) {
			throw new AuthException("You do not have the privileges");
		}

		User u = em.find(User.class, email);
		if (u != null) {
			String hashed = BCrypt.hashpw(newPassword, BCrypt.gensalt());
			u.setPwd(hashed);
			logger.debug("Hash is " + hashed);
		} else {
			fail = true;
		}

		if (fail) {
			throw new AuthException("User not recognized");
		}

	}

	public void unregister(String sessionid, String email) throws InternalException, AuthException {

		boolean fail = false;

		if (sessionid == null) {
			fail = true;
		} else {
			Session s = em.find(Session.class, sessionid);
			if (s == null || s.getPriv() != Priv.ALL || s.getEmail().equals(email)) {
				fail = true;
			}
		}

		if (fail) {
			throw new AuthException("You do not have the privileges");
		}

		User u = em.find(User.class, email);
		if (u != null) {
			em.remove(u);
		} else {
			fail = true;
		}

		if (fail) {
			throw new AuthException("User not recognized");
		}

	}

	public void chEmail(String sessionid, String email, String newEmail) throws InternalException,
			AuthException {

		boolean fail = false;

		// Check privs

		Session s = em.find(Session.class, sessionid);
		if (s == null) {
			fail = true;
		} else if (s.getPriv() != Priv.ALL && !s.getEmail().equals(email)) {
			fail = true;
		}

		if (fail) {
			throw new AuthException("You do not have the privileges");
		}

		// Create but do not persist new user

		User newU = null;

		User u = em.find(User.class, email);
		if (u != null) {
			newU = new User(newEmail, u.getPwd(), u.getPriv());
		} else {
			fail = true;
		}

		if (fail) {
			throw new AuthException("User not recognized");
		}

		// Persist new user

		em.persist(newU);

		// Delete the old user

		u = em.find(User.class, email);
		em.remove(u);

	}

	public void chPriv(String sessionid, String email, String newPriv) throws InternalException,
			AuthException {

		boolean fail = false;

		Session s = em.find(Session.class, sessionid);
		if (s == null || s.getPriv() != Priv.ALL || s.getEmail().equals(email)) {
			fail = true;
		}

		if (fail) {
			throw new AuthException("You do not have the privileges");
		}

		User u = em.find(User.class, email);
		if (u != null) {
			u.setPriv(Priv.valueOf(newPriv));
		} else {
			fail = true;
		}

		if (fail) {
			throw new AuthException("User not recognized");
		}
	}

}
