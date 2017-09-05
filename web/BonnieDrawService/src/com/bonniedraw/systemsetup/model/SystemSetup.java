package com.bonniedraw.systemsetup.model;

public class SystemSetup {
    private Integer systemSetupId;

    private String mailHost;

    private Integer mailPort;

    private String mailUsername;

    private String mailPassword;

    private String mailProtocol;

    public Integer getSystemSetupId() {
        return systemSetupId;
    }

    public void setSystemSetupId(Integer systemSetupId) {
        this.systemSetupId = systemSetupId;
    }

    public String getMailHost() {
        return mailHost;
    }

    public void setMailHost(String mailHost) {
        this.mailHost = mailHost == null ? null : mailHost.trim();
    }

    public Integer getMailPort() {
        return mailPort;
    }

    public void setMailPort(Integer mailPort) {
        this.mailPort = mailPort;
    }

    public String getMailUsername() {
        return mailUsername;
    }

    public void setMailUsername(String mailUsername) {
        this.mailUsername = mailUsername == null ? null : mailUsername.trim();
    }

    public String getMailPassword() {
        return mailPassword;
    }

    public void setMailPassword(String mailPassword) {
        this.mailPassword = mailPassword == null ? null : mailPassword.trim();
    }

    public String getMailProtocol() {
        return mailProtocol;
    }

    public void setMailProtocol(String mailProtocol) {
        this.mailProtocol = mailProtocol == null ? null : mailProtocol.trim();
    }
}