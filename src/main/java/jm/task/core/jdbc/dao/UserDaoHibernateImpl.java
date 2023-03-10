package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.List;

public class UserDaoHibernateImpl implements UserDao {
    private final SessionFactory sessionFactory = Util.getNewSessionFactory();
    private Transaction transaction = null;

    @Override
    public void createUsersTable() throws RuntimeException {
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.createSQLQuery("CREATE TABLE users (id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(45), lastName VARCHAR(45), age INT);").executeUpdate();
            transaction.commit();

        } catch (RuntimeException ignored) {
        }
    }

    @Override
    public void dropUsersTable() {
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.createSQLQuery("DROP TABLE users;").executeUpdate();
            transaction.commit();

        } catch (RuntimeException ignored) {
        }
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        User user = new User(name, lastName, age);

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();

        } catch (RuntimeException e) {
            transaction.rollback();
        }
    }

    @Override
    public void removeUserById(long id) {

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            User user = session.load(User.class, id);
            session.delete(user);
            transaction.commit();

        } catch (RuntimeException e) {
            transaction.rollback();
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = Collections.emptyList();
        Session session = null;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            users = session.createQuery("from User").getResultList();
            transaction.commit();

        } catch (RuntimeException e) {
            System.out.println("RuntimeException: " + e);
            transaction.rollback();

        } finally {
            session.close();
        }
        return users;
    }

    @Override
    public void cleanUsersTable() {
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.createSQLQuery("DELETE FROM users;").executeUpdate();
            transaction.commit();

        } catch (RuntimeException e) {
            transaction.rollback();
        }
    }
}
