package generator.domain;

import java.util.Date;

/**
 * users
 * @TableName user
 */
public class User {
    /**
     * id
     */
    private Long id;

    /**
     * account
     */
    private String userAccount;

    /**
     * key
     */
    private String userPassword;

    /**
     * Wechat public platform id
     */
    private String unionId;

    /**
     * Wechat public account openId
     */
    private String mpOpenId;

    /**
     * user name
     */
    private String userName;

    /**
     * user avatar
     */
    private String userAvatar;

    /**
     * user profile
     */
    private String userProfile;

    /**
     * user role：user/admin/ban
     */
    private String userRole;

    /**
     * edit time
     */
    private Date editTime;

    /**
     * create time
     */
    private Date createTime;

    /**
     * update time
     */
    private Date updateTime;

    /**
     * is delete (0: no, 1: yes)
     */
    private Integer isDelete;

    /**
     * vip expiration time
     */
    private Date vipExpireTime;

    /**
     * vip code
     */
    private String vipCode;

    /**
     * vip number
     */
    private Long vipNumber;

    /**
     * Stripe Customer ID
     */
    private String stripeCustomerId;

    /**
     * id
     */
    public Long getId() {
        return id;
    }

    /**
     * id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * account
     */
    public String getUserAccount() {
        return userAccount;
    }

    /**
     * account
     */
    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    /**
     * key
     */
    public String getUserPassword() {
        return userPassword;
    }

    /**
     * key
     */
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    /**
     * Wechat public platform id
     */
    public String getUnionId() {
        return unionId;
    }

    /**
     * Wechat public platform id
     */
    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    /**
     * Wechat public account openId
     */
    public String getMpOpenId() {
        return mpOpenId;
    }

    /**
     * Wechat public account openId
     */
    public void setMpOpenId(String mpOpenId) {
        this.mpOpenId = mpOpenId;
    }

    /**
     * user name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * user name
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * user avatar
     */
    public String getUserAvatar() {
        return userAvatar;
    }

    /**
     * user avatar
     */
    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    /**
     * user profile
     */
    public String getUserProfile() {
        return userProfile;
    }

    /**
     * user profile
     */
    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    /**
     * user role：user/admin/ban
     */
    public String getUserRole() {
        return userRole;
    }

    /**
     * user role：user/admin/ban
     */
    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    /**
     * edit time
     */
    public Date getEditTime() {
        return editTime;
    }

    /**
     * edit time
     */
    public void setEditTime(Date editTime) {
        this.editTime = editTime;
    }

    /**
     * create time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * create time
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * update time
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * update time
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * is delete (0: no, 1: yes)
     */
    public Integer getIsDelete() {
        return isDelete;
    }

    /**
     * is delete (0: no, 1: yes)
     */
    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    /**
     * vip expiration time
     */
    public Date getVipExpireTime() {
        return vipExpireTime;
    }

    /**
     * vip expiration time
     */
    public void setVipExpireTime(Date vipExpireTime) {
        this.vipExpireTime = vipExpireTime;
    }

    /**
     * vip code
     */
    public String getVipCode() {
        return vipCode;
    }

    /**
     * vip code
     */
    public void setVipCode(String vipCode) {
        this.vipCode = vipCode;
    }

    /**
     * vip number
     */
    public Long getVipNumber() {
        return vipNumber;
    }

    /**
     * vip number
     */
    public void setVipNumber(Long vipNumber) {
        this.vipNumber = vipNumber;
    }

    /**
     * Stripe Customer ID
     */
    public String getStripeCustomerId() {
        return stripeCustomerId;
    }

    /**
     * Stripe Customer ID
     */
    public void setStripeCustomerId(String stripeCustomerId) {
        this.stripeCustomerId = stripeCustomerId;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        User other = (User) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserAccount() == null ? other.getUserAccount() == null : this.getUserAccount().equals(other.getUserAccount()))
            && (this.getUserPassword() == null ? other.getUserPassword() == null : this.getUserPassword().equals(other.getUserPassword()))
            && (this.getUnionId() == null ? other.getUnionId() == null : this.getUnionId().equals(other.getUnionId()))
            && (this.getMpOpenId() == null ? other.getMpOpenId() == null : this.getMpOpenId().equals(other.getMpOpenId()))
            && (this.getUserName() == null ? other.getUserName() == null : this.getUserName().equals(other.getUserName()))
            && (this.getUserAvatar() == null ? other.getUserAvatar() == null : this.getUserAvatar().equals(other.getUserAvatar()))
            && (this.getUserProfile() == null ? other.getUserProfile() == null : this.getUserProfile().equals(other.getUserProfile()))
            && (this.getUserRole() == null ? other.getUserRole() == null : this.getUserRole().equals(other.getUserRole()))
            && (this.getEditTime() == null ? other.getEditTime() == null : this.getEditTime().equals(other.getEditTime()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getIsDelete() == null ? other.getIsDelete() == null : this.getIsDelete().equals(other.getIsDelete()))
            && (this.getVipExpireTime() == null ? other.getVipExpireTime() == null : this.getVipExpireTime().equals(other.getVipExpireTime()))
            && (this.getVipCode() == null ? other.getVipCode() == null : this.getVipCode().equals(other.getVipCode()))
            && (this.getVipNumber() == null ? other.getVipNumber() == null : this.getVipNumber().equals(other.getVipNumber()))
            && (this.getStripeCustomerId() == null ? other.getStripeCustomerId() == null : this.getStripeCustomerId().equals(other.getStripeCustomerId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserAccount() == null) ? 0 : getUserAccount().hashCode());
        result = prime * result + ((getUserPassword() == null) ? 0 : getUserPassword().hashCode());
        result = prime * result + ((getUnionId() == null) ? 0 : getUnionId().hashCode());
        result = prime * result + ((getMpOpenId() == null) ? 0 : getMpOpenId().hashCode());
        result = prime * result + ((getUserName() == null) ? 0 : getUserName().hashCode());
        result = prime * result + ((getUserAvatar() == null) ? 0 : getUserAvatar().hashCode());
        result = prime * result + ((getUserProfile() == null) ? 0 : getUserProfile().hashCode());
        result = prime * result + ((getUserRole() == null) ? 0 : getUserRole().hashCode());
        result = prime * result + ((getEditTime() == null) ? 0 : getEditTime().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getIsDelete() == null) ? 0 : getIsDelete().hashCode());
        result = prime * result + ((getVipExpireTime() == null) ? 0 : getVipExpireTime().hashCode());
        result = prime * result + ((getVipCode() == null) ? 0 : getVipCode().hashCode());
        result = prime * result + ((getVipNumber() == null) ? 0 : getVipNumber().hashCode());
        result = prime * result + ((getStripeCustomerId() == null) ? 0 : getStripeCustomerId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", userAccount=").append(userAccount);
        sb.append(", userPassword=").append(userPassword);
        sb.append(", unionId=").append(unionId);
        sb.append(", mpOpenId=").append(mpOpenId);
        sb.append(", userName=").append(userName);
        sb.append(", userAvatar=").append(userAvatar);
        sb.append(", userProfile=").append(userProfile);
        sb.append(", userRole=").append(userRole);
        sb.append(", editTime=").append(editTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", isDelete=").append(isDelete);
        sb.append(", vipExpireTime=").append(vipExpireTime);
        sb.append(", vipCode=").append(vipCode);
        sb.append(", vipNumber=").append(vipNumber);
        sb.append(", stripeCustomerId=").append(stripeCustomerId);
        sb.append("]");
        return sb.toString();
    }
}