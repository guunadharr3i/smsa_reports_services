package com.smsaSchedulers.Schedulers.Entity;

import javax.persistence.*;

@Entity
@Table(name = "SMSA_NOSTRO_ACCOUNT_MASTER")
public class SwiftNostroAccountMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "nostro_acc_seq")
    @SequenceGenerator(name = "nostro_acc_seq", sequenceName = "SEQ_NOSTRO_ACC_ID", // Oracle sequence name
            // sequenceName = "SEQ_NOSTRO_ACC_TEMP_ID", // Oracle sequence name
            allocationSize = 1)
    @Column(name = "SMSA_NOSTRO_ID", columnDefinition = "NUMBER(19,0)")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY
    //  cascade = CascadeType.ALL
    )
    @JoinColumn(name = "SMSA_MESSAGE_ID", referencedColumnName = "SMSA_MESSAGE_ID")
    private SwiftMessageHeader header;

    @Column(name = "SMSA_NOSTRO_ACCOUNT", length = 255)
    private String account;

    @Column(name = "SMSA_NOSTRO_RECEIVER", length = 16)
    private String receiver;

    @Column(name = "SMSA_NOSTRO_SENDER", length = 16)
    private String sender;

    // @Column(name = "SMSA_NOSTRO_BANK", length = 255)
    // private String bankName;
/*
 * CHECK (SMSA_ACCOUNT_IS_NOSTRO IN (0, 1))
     */
    @Column(name = "SMSA_ACCOUNT_IS_NOSTRO", columnDefinition = "NUMBER(1) DEFAULT 1", nullable = false)
    private Integer isNostro = 1; // Default as per table definition

    // @Column(name = "SMSA_NOSTRO_MSG_IO", length = 1)
    // private String messageIdentifier;
    // @Column(name = "SMSA_NOSTRO_MSG_TYPE", columnDefinition = "NUMBER(4)")
    // private Integer messageType;
    // @Column(name = "SMSA_NOSTRO_MSG_CCY", length = 5)
    // private String messageCurrency;
    // @Check(constraints = "smsa_nostro_account_cnfm IN (0,1)")
    @Column(name = "SMSA_NOSTRO_ACCOUNT_CNFM", columnDefinition = "NUMBER(1) DEFAULT 0", nullable = false)
    private boolean isConfirmAcccount;

    @Column(name = "SMSA_NOSTRO_ACCOUNT_TEAM", length = 255)
    private String accountTeam;

    @Column(name = "SMSA_NOSTRO_ACCOUNT_RMRK", length = 255)
    private String accountRemark;

    public SwiftMessageHeader getHeader() {
        return header;
    }

    public void setHeader(SwiftMessageHeader header) {
        this.header = header;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    // public String getBankName() {
    //     return bankName;
    // }
    // public void setBankName(String bankName) {
    //     this.bankName = bankName;
    // }
    public Integer getIsNostro() {
        return isNostro;
    }

    public void setIsNostro(Integer isNostro) {
        this.isNostro = isNostro;
    }

    // public String getMessageIdentifier() {
    //     return messageIdentifier;
    // }
    // public void setMessageIdentifier(String messageIdentifier) {
    //     this.messageIdentifier = messageIdentifier;
    // }
    // public Integer getMessageType() {
    //     return messageType;
    // }
    // public void setMessageType(Integer messageType) {
    //     this.messageType = messageType;
    // }
    // public String getMessageCurrency() {
    //     return messageCurrency;
    // }
    // public void setMessageCurrency(String messageCurrency) {
    //     this.messageCurrency = messageCurrency;
    // }
    public boolean isConfirmAcccount() {
        return isConfirmAcccount;
    }

    public void setConfirmAcccount(boolean isConfirmAcccount) {
        this.isConfirmAcccount = isConfirmAcccount;
    }

    public String getAccountTeam() {
        return accountTeam;
    }

    public void setAccountTeam(String accountTeam) {
        this.accountTeam = accountTeam;
    }

    public String getAccountRemark() {
        return accountRemark;
    }

    public void setAccountRemark(String accountRemark) {
        this.accountRemark = accountRemark;
    }

}
