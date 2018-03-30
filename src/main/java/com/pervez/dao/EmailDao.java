package com.pervez.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pervez.entity.EmailEntity;

@Repository
@Transactional
public class EmailDao {

	@Autowired
	SessionFactory sessionFactory;
	
	
	
	public void saveOrUpdate(EmailEntity emailEntity){
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(emailEntity);
	}
	
	public List<EmailEntity> getUnsentEmail(){
		Session session = sessionFactory.getCurrentSession();
		List<EmailEntity> emailEntities = (List<EmailEntity>)session.createQuery("from EmailEntity where isSent = false and retryCount < 10");
		return emailEntities;
	}
	
	
}
