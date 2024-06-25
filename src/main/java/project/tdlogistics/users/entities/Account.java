package project.tdlogistics.users.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import project.tdlogistics.users.validations.NullOrEmpty;
import project.tdlogistics.users.validations.account.Create;
import project.tdlogistics.users.validations.account.Delete;
import project.tdlogistics.users.validations.account.Login;
import project.tdlogistics.users.validations.account.Search;
import project.tdlogistics.users.validations.account.Update;

@Entity
@Table(name = "account")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @NotBlank(
        message = "Id không được để trống",
        groups = {Update.class, Delete.class}
    )
    @NotBlank(
        message = "Id không được để trống",
        groups = {Update.class, Delete.class}
    )
    @Pattern(
        message = "Id không hợp lệ",
        regexp = "[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}",
        groups = {Search.class, Update.class, Delete.class}
    )
    @Column(name = "id")
    private String id;

    @NotBlank(
        message = "Tên đăng nhập không được để trống",
        groups = {Create.class, Login.class}
    )
    @NotBlank(
        message = "Tên đăng nhập không được để trống",
        groups = {Create.class, Login.class}
    )
    @NullOrEmpty(
        message = "Trường tên đăng nhập không được cho phép",
        groups = {Delete.class}
    )
    @Pattern(
        message = "Tên đăng nhập không đúng định dạng",
        regexp = "[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){3,28}[a-zA-Z0-9]",
        groups = {Create.class, Search.class, Update.class, Login.class}
    )
    @Column(name = "username")
    private String username;

    @NotNull(
        message = "Mật khẩu không được để trống",
        groups = {Create.class, Login.class}
    )
    @NotBlank(
        message = "Mật khẩu không được để trống",
        groups = {Create.class, Login.class}
    )
    @NullOrEmpty(
        message = "Trường mật khẩu không được cho phép",
        groups = {Delete.class, Search.class}
    )
    @Pattern(
        message = "Mật khẩu không đúng định dạng",
        regexp = "(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z\\d])\\S{8,}",
        groups = {Create.class, Search.class, Update.class, Login.class}
    )
    @Column(name = "password")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String password;

    @NotNull(
        message = "Số điện thoại không được để trống",
        groups = {Create.class}
    )
    @NotBlank(
        message = "Số điện thoại không được để trống",
        groups = {Create.class}
    )
    @NullOrEmpty(
        message = "Trường số điện thoại không được cho phép",
        groups = {Delete.class}
    )
    @Pattern(
        message = "Số điện thoại không hợp lệ",
        regexp = "[0-9]{1,10}",
        groups = {Create.class, Search.class, Update.class}
    )
    @Column(name = "phone_number")
    private String phoneNumber;

    @NotNull(
        message = "Email không được để trống",
        groups = {Create.class}
    )
    @NotBlank(
        message = "Email không được để trống",
        groups = {Create.class}
    )
    @NullOrEmpty(
        message = "Trường email không được cho phép",
        groups = {Delete.class}
    )
    @Pattern(
        message = "Email không hợp lệ",
        regexp = "[a-zA-Z0-9._-]{1,64}@[a-zA-Z0-9._-]{1,255}\\.[a-zA-Z]{2,4}",
        groups = {Create.class, Search.class, Update.class}
    )
    @Column(name = "email")
    private String email;

    @Valid
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @NullOrEmpty(
        message = "Trường active không được cho phép",
        groups = {Create.class, Search.class, Update.class, Delete.class}
    )
    private Boolean active;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column(name = "last_update")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime lastUpdate;

    public Account() {
    }

    public Account(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public Account(String username, String password, String phoneNumber, String email, Role role, Boolean active) {
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.role = role;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = this.lastUpdate = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        this.lastUpdate = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Account [id=" + id + ", username=" + username + ", password=" + password + ", phoneNumber="
                + phoneNumber + ", email=" + email + ", role=" + role + ", active=" + active + ", createdAt="
                + createdAt + ", lastUpdate=" + lastUpdate + "]";
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (this.role != null) {
            authorities.add(new SimpleGrantedAuthority(role.name()));
        }
        return authorities;
    }
}
