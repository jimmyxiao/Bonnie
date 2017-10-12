package com.bonniedraw.user.model;

import java.util.Date;

public class UserInfo {
    private Integer userId;

    private Integer userType;

    private String userCode;

    private String userPw;

    private String userName;

    private String nickName;

    private String email;

    private String secEmail;

    private String description;

    private String webLink;

    private String phoneCountryCode;

    private String phoneNo;

    private Integer gender;

    private String regFacebookId;

    private String regGoogleId;

    private String regTwitterId;

    private String regWechatId;

    private String regRes1Id;

    private String regRes2Id;

    private String regRes3Id;

    private String profilePicture;

    private Date birthday;

    private Integer status;

    private String languageCode;

    private String countryCode;

    private String regData;

    private Date regValidDate;

    private Date creationDate;

    private Integer createdBy;

    private Date updateDate;

    private Integer updatedBy;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode == null ? null : userCode.trim();
    }

    public String getUserPw() {
        return userPw;
    }

    public void setUserPw(String userPw) {
        this.userPw = userPw == null ? null : userPw.trim();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName == null ? null : nickName.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getSecEmail() {
        return secEmail;
    }

    public void setSecEmail(String secEmail) {
        this.secEmail = secEmail == null ? null : secEmail.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink == null ? null : webLink.trim();
    }

    public String getPhoneCountryCode() {
        return phoneCountryCode;
    }

    public void setPhoneCountryCode(String phoneCountryCode) {
        this.phoneCountryCode = phoneCountryCode == null ? null : phoneCountryCode.trim();
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo == null ? null : phoneNo.trim();
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getRegFacebookId() {
        return regFacebookId;
    }

    public void setRegFacebookId(String regFacebookId) {
        this.regFacebookId = regFacebookId == null ? null : regFacebookId.trim();
    }

    public String getRegGoogleId() {
        return regGoogleId;
    }

    public void setRegGoogleId(String regGoogleId) {
        this.regGoogleId = regGoogleId == null ? null : regGoogleId.trim();
    }

    public String getRegTwitterId() {
        return regTwitterId;
    }

    public void setRegTwitterId(String regTwitterId) {
        this.regTwitterId = regTwitterId == null ? null : regTwitterId.trim();
    }

    public String getRegWechatId() {
        return regWechatId;
    }

    public void setRegWechatId(String regWechatId) {
        this.regWechatId = regWechatId == null ? null : regWechatId.trim();
    }

    public String getRegRes1Id() {
        return regRes1Id;
    }

    public void setRegRes1Id(String regRes1Id) {
        this.regRes1Id = regRes1Id == null ? null : regRes1Id.trim();
    }

    public String getRegRes2Id() {
        return regRes2Id;
    }

    public void setRegRes2Id(String regRes2Id) {
        this.regRes2Id = regRes2Id == null ? null : regRes2Id.trim();
    }

    public String getRegRes3Id() {
        return regRes3Id;
    }

    public void setRegRes3Id(String regRes3Id) {
        this.regRes3Id = regRes3Id == null ? null : regRes3Id.trim();
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture == null ? null : profilePicture.trim();
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getRegData() {
        return regData;
    }

    public void setRegData(String regData) {
        this.regData = regData == null ? null : regData.trim();
    }

    public Date getRegValidDate() {
        return regValidDate;
    }

    public void setRegValidDate(Date regValidDate) {
        this.regValidDate = regValidDate;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }
}