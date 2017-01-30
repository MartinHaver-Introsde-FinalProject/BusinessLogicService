package model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.xml.bind.annotation.XmlRootElement;

import dao.LifeCoachDao;

@Entity
@Table(name = "Motivation")
@NamedQuery(name = "Motivation.findAll", query = "SELECT a FROM Motivation a")
@XmlRootElement(name = "motivation")
public class Motivation implements Serializable{
private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(generator = "sqlite_motivation")
	@TableGenerator(name = "sqlite_motivation", table = "sqlite_sequence", pkColumnName = "name", valueColumnName = "seq", pkColumnValue = "Motivation")
	@Column(name = "idMotivation")
	private int idMotivation;
	
	@Column(name = "text")
	private String text;
	
	public Motivation() {
		
	}

	public int getIdMotivation() {
		return idMotivation;
	}

	public void setIdMotivation(int idMotivation) {
		this.idMotivation = idMotivation;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public static Motivation getMotivationById(int idMotivation) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		Motivation motivation = em.find(Motivation.class, idMotivation);
		LifeCoachDao.instance.closeConnections(em);
		return motivation;
	}

	public static List<Motivation> getAll() {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		List<Motivation> list = em.createNamedQuery("Motivation.findAll", Motivation.class)
				.getResultList();
		LifeCoachDao.instance.closeConnections(em);
		return list;
	}

	public static Motivation saveMotivation(Motivation motivation) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.persist(motivation);
		tx.commit();
		LifeCoachDao.instance.closeConnections(em);
		return motivation;
	}

	public static Motivation updateMotivation(Motivation motivation) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		motivation = em.merge(motivation);
		tx.commit();
		LifeCoachDao.instance.closeConnections(em);
		return motivation;
	}

	public static void removeMotivation(Motivation motivation) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		motivation = em.merge(motivation);
		em.remove(motivation);
		tx.commit();
		LifeCoachDao.instance.closeConnections(em);
	}
	
}
