package com.arbitragebroker.admin.entity;

import lombok.Data;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tbl_role")
@Audited
public class RoleEntity extends BaseEntity<Long> {
	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name="seq_role",sequenceName="seq_role",allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_role")
	public Long id;

	@Column(name="role",unique = true)
	private String role;

	@Column(name="title")
	private String title;

	@Override
	public String getSelectTitle() {
		return title;
	}

//	@JsonIgnore
//	@ManyToMany(cascade = CascadeType.DETACH)
//	@JoinTable(name = "tbl_user_role", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
//	private Set<UserEntity> userEntities;


}
