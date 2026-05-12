package org.sparkfi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_settings")
public class UserSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String preferredCurrency;
    private Boolean darkMode;
    private Boolean notificationsEnabled;
    private String language;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    public UserSettings() {
    }

    public UserSettings(User user, String preferredCurrency, Boolean darkMode, Boolean notificationsEnabled, String language) {
        this.user = user;
        this.preferredCurrency = preferredCurrency;
        this.darkMode = darkMode;
        this.notificationsEnabled = notificationsEnabled;
        this.language = language;
    }

    public Long getId() {
        return id;
    }

    public String getPreferredCurrency() {
        return preferredCurrency;
    }

    public void setPreferredCurrency(String preferredCurrency) {
        this.preferredCurrency = preferredCurrency;
    }

    public Boolean getDarkMode() {
        return darkMode;
    }

    public void setDarkMode(Boolean darkMode) {
        this.darkMode = darkMode;
    }

    public Boolean getNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(Boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
