package com.pervez.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the email_entity database table.
 * 
 */
@Entity
@Table(name="email_entity")
@NamedQuery(name="EmailEntity.findAll", query="SELECT e FROM EmailEntity e")
public class EmailEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	@Lob
	private String content;

	@Column(name="is_sent")
	private boolean isSent;

	@Column(name="retry_count")
	private int retryCount;

	@Lob
	private String subject;

	@Lob
	private String to;

	public EmailEntity() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean getIsSent() {
		return this.isSent;
	}

	public void setIsSent(boolean isSent) {
		this.isSent = isSent;
	}

	public int getRetryCount() {
		return this.retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTo() {
		return this.to;
	}

	public void setTo(String to) {
		this.to = to;
	}

}