rules = {
    firstName: "required",
    lastName: "required",
    userName: "required",
    roleSelect2: "required",
    countrySelect2: "required",
    status: "required",
    password: {
        minlength: 5
    },
    email: {
        required: true,
        email: true
    },
    repeatPassword: {
        minlength: 5,
        equalTo: "#password"
    }
};
messages = {
    firstName: resources.pleaseEnter.format(resources.name),
    lastName: resources.pleaseEnter.format(resources.lastName),
    userName: resources.pleaseEnter.format(resources.userName),
    roleSelect2: resources.pleaseSelect.format(resources.role),
    countrySelect2: resources.pleaseSelect.format(resources.country),
    status: resources.pleaseSelect.format(resources.status),
    password: {
        minlength: resources.mustBeMoreThan.format(resources.password, 5, resources.character)
    },
    email: {
        required: resources.pleaseEnter.format(resources.email),
        email: resources.invalidFormat.format(resources.email)
    },
    repeatPassword: {
        minlength: resources.mustBeMoreThan.format(resources.password, 5, resources.character),
        equalTo: resources.confirmPasswordDoesNotMach
    }
}
function loadEntityByInput() {
    let model = {
        id: isNullOrEmpty($("#hdf_id").val()) ? null : $("#hdf_id").val(),
        email: $("#email").val(),
        userName: $("#userName").val(),
        password: $("#password").val(),
        parent: isNullOrEmpty($("#parentSelect2").val()) ? null : {id: $("#parentSelect2").val()},
        firstName: $("#firstName").val(),
        lastName: $("#lastName").val(),
        active: $("#status").val(),
        emailVerified: $("#emailVerified").val(),
        walletAddress: $("#walletAddress").val(),
        roles: arrayToJsonArray($("#roleSelect2").val()),
        country: isNullOrEmpty($("#countrySelect2").val()) ? null : {id: $("#countrySelect2").val()},
        profileImageUrl:  $("#profileImageUrl").val(),
    };
    return model;
}
function loadSearchEntityByInput() {
    let filter = {
        email: $("#email").val(),
        userName: $("#userName").val(),
        parentId: isNullOrEmpty($("#parentSelect2").val()) ? null : $("#parentSelect2").val(),
        firstName: $("#firstName").val(),
        lastName: $("#lastName").val(),
        active: $("#status").val(),
        emailVerified: $("#emailVerified").val(),
        walletAddress: $("#walletAddress").val(),
        roles: $("#roleSelect2").val(),
        countryId: isNullOrEmpty($("#countrySelect2").val()) ? null : $("#countrySelect2").val(),
        profileImageUrl:  $("#profileImageUrl").val(),
    };
    return filter;
}

function loadInputByEntity(entity) {
    $("#hdf_id").val(entity.id);
    $("#email").val(entity.email);
    $("#userName").val(entity.userName);
    $("#firstName").val(entity.firstName);
    $("#lastName").val(entity.lastName);
    $("#roleSelect2").empty();

    entity.roles.forEach(role => $("#roleSelect2").append("<option value='" + role.id + "'>" + role.title + "</option>").trigger('change'));
    $("#roleSelect2").val(entity.roles.map(r => r.id)).trigger('change');
    $('#status').val(entity.active + '');
    $('#emailVerified').val(entity.emailVerified + '');
    $("#parentSelect2").html("<option value='" + get(() => entity.parent.id) + "' selected>" + get(() => entity.parent.selectTitle	) + "</option>").trigger('change');
    $("#parentSelect2").val(get(() => entity.parent.id)).trigger('change');
    $("#countrySelect2").html("<option value='" + get(() => entity.country.id) + "' selected>" + get(() => entity.country.name) + "</option>").trigger('change');
    $("#countrySelect2").val(get(() => entity.country.id)).trigger('change');
    $('#walletAddress').val(entity.walletAddress);
    $('#profileImageUrl').val(entity.profileImageUrl);
}
columns = [{
    data: 'profileImageUrl',
    searchable: false,
    sortable: false,
    render: function (data) { return `<img src="${isNullOrEmpty(data) ? '/images/find_user.png' : data}" style="width: 35px;"></img>` }
},{
    data: 'email'
},{
    data: 'uid'
},{
    data: 'walletAddress'
},{
    data: 'firstName',
    render: function (data, type, row) { return data + " " + row.lastName }
}, {
    data: 'userName'
}, {
    data: 'active',
    render: function (data) { return data ? resources.active : resources.inactive }
}, {
    data: 'modifiedDate',
    render: function (data) { return toLocalizingDateString(data, true) }
},{
    data: 'country.name'
},{
    data: 'deposit'
},{
    data: 'withdrawal'
},{
    data: 'reward'
},{
    data: 'emailVerified',
    render: function (data) { return data ? resources.yes : resources.no }
}, {
    data: 'id',
    searchable: false,
    sortable: false,
    render: function (data) { return "<a class='btn btn-default fa fa-pencil' id='" + data + "'></a> <a class='btn btn-danger fa fa-trash' id='" + data + "'></a>" }
}];

/*
function changeSwitch(element, flag) {
    var programmaticallyChange = !isNullOrEmpty(flag);
    var isCheck = programmaticallyChange ? flag : element.is(':checked');
    if (programmaticallyChange && element.is(':checked') != flag) {
        element.click();
    }
    if (isCheck) {
        element.next().next().text(resources.active);
        element.attr('checked', 'checked');
    } else {
        element.removeAttr('checked');
        element.next().next().text(resources.inactive);
    }
}*/
