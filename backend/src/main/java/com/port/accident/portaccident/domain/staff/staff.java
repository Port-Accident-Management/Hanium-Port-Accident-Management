package com.port.accident.portaccident.domain.staff;

import com.port.accident.portaccident.domain.code.DetailedCode;
import com.port.accident.portaccident.dto.staff.StaffDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;

@Table(name = "staff")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staff_id")
    private Integer id;

    @Column(name = "staff_name")
    private String name;

    @Column(name = "staff_co")
    private String corporation;

    @Column(name = "staff_group")
    private String group;

    @Column(name = "staff_position")
    private String position;

    @Column(name = "staff_email")
    private String email;

    @Column(name = "staff_phone")
    private String phoneNumber;

    @Builder
    public Staff(Integer id, String name, String corporation, String group, String position, String email, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.corporation = corporation;
        this.group = group;
        this.position = position;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    @Transactional(readOnly = true)
    public void update(StaffDto staffDto) {
        this.name = staffDto.getName();
        this.corporation = staffDto.getCorporation();
        this.group = staffDto.getGroup();
        this.position = staffDto.getPosition();
        this.email = staffDto.getEmail();
        this.phoneNumber = staffDto.getPhoneNumber();
    }
}
