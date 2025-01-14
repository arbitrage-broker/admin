package com.arbitragebroker.admin.model;

import com.arbitragebroker.admin.util.StringUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
public class UserModel extends BaseModel<UUID> {
    @NotNull
    @NotBlank
    private String userName;
    @Email
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @NotNull
    @NotBlank
    private String firstName;
    @NotNull
    @NotBlank
    private String lastName;
    private Boolean active;
    private String uid;
    private String referralCode;
    private UserModel parent;
    private String treePath;
    private String walletAddress;
    private int childCount;
    private String profileImageUrl;
    private CountryModel country;
    @NotNull
    @NotEmpty
    private Set<RoleModel> roles;
    private BigDecimal deposit;
    private BigDecimal withdrawal;
    private BigDecimal reward;
    private Boolean emailVerified;
    private String role;

    public UserModel setUserId(UUID id) {
        setId(id);
        return this;
    }
    public List<UUID> getParents(){
        if(treePath == null)
            return null;
        String parents = treePath.replace("," + getId(),"");
        return StringUtils.reverseArrayFromString(parents);
    }
}
