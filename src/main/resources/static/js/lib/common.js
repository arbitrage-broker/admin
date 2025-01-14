(function ($) {

    let o = $({});

    $.subscribe = function () {
        o.on.apply(o, arguments);
    };

    $.unsubscribe = function () {
        o.off.apply(o, arguments);
    };

    $.publish = function () {
        o.trigger.apply(o, arguments);
    };

}(jQuery));

$.postJSON = function(url, data, success, dataType) {
    return $.ajax({
        url: url,
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(data),  // Convert the JavaScript object to JSON string
        success: success,
        dataType: dataType
    });
};
var currentUser = JSON.parse($("#currentUser").val());
$.cookie.json = true;

$(function () {
    $('.date input').keydown(function (event) {
        event.preventDefault();
    });
});

if (!String.prototype.format) {
    String.prototype.format = function () {
        let args = arguments;
        return this.replace(/{(\d+)}/g, function (match, number) {
            return typeof args[number] != 'undefined'
                ? args[number]
                : match
                ;
        });
    };
}


function loadPages(page) {
    window.location.href =  page;
}

function loadScriptRunTime(url, callback) {
    // Adding the script tag to the head as suggested before
    let head = document.getElementsByTagName('head')[0];
    let script = document.createElement('script');
    script.type = 'text/javascript';
    script.src = url;

    // Then bind the event to the callback function.
    // There are several events for cross browser compatibility.
    script.onreadystatechange = callback;
    script.onload = callback;

    // Fire the loading
    head.appendChild(script);
}


function changeUrlParameterAndReload(param, value) {
    changeUrlParameter(param, value);
    location.reload();
}

function changeUrlParameter(param, value) {
    let currentURL = window.location.href + '&';
    let change = new RegExp('(' + param + ')=(.*)&', 'g');
    let newURL = currentURL.replace(change, '$1=' + value + '&');

    if (getURLParameter(param) !== "") {
        try {
            window.history.replaceState('', '', newURL.slice(0, -1));
        } catch (e) {
            console.log(e);
        }
    } else {
        let currURL = window.location.href;
        if (currURL.indexOf("?") !== -1) {
            window.history.replaceState('', '', currentURL.slice(0, -1) + '&' + param + '=' + value);
        } else {
            window.history.replaceState('', '', currentURL.slice(0, -1) + '?' + param + '=' + value);
        }
    }
}

function getURLParameter(name) {
    let regexS = "[\\?&]" + name + "=([^&#]*)",
        regex = new RegExp(regexS),
        results = regex.exec(window.location.search);
    if (results == null) {
        return "";
    } else {
        return decodeURIComponent(results[1].replace(/\+/g, " "));
    }
}

function removeUrlparameter(parameter) {
    let url = document.location.href;
    let urlparts = url.split('?');

    if (urlparts.length >= 2) {
        let urlBase = urlparts.shift();
        let queryString = urlparts.join("?");

        let prefix = encodeURIComponent(parameter) + '=';
        let pars = queryString.split(/[&;]/g);
        for (let i = pars.length; i-- > 0;)
            if (pars[i].lastIndexOf(prefix, 0) !== -1)
                pars.splice(i, 1);
        url = urlBase + pars.join('&');
        window.history.pushState('', document.title, url); // added this line to push the new url directly to url bar .

    }
    return url;
}
function show_info(mes, fixStatus) {
    // $("#myAlert .alert-content").html(mes);
    // if (isNullOrEmpty(fixStatus) && fixStatus) {
    //     $("#myAlert").removeClass("alert-danger alert-warning alert-success").addClass("alert-info").show();
    // } else {
    //     $("#myAlert").removeClass("alert-danger alert-warning alert-success").addClass("alert-info").show().delay(3000).fadeOut();
    // }
    new PNotify({
        title: 'New Thing',
        text: mes,
        type: 'info',
        styling: 'bootstrap3',
        nonblock: {nonblock: !0},
        before_close: function (e) {
            return e.update({title: e.options.title + " - Enjoy your Stay", before_close: null}), e.queueRemove(), !1
        }
    });
}

function show_success(mes) {
    // $("#myAlert .alert-content").html(mes);
    // $("#myAlert").removeClass("alert-danger alert-warning alert-info").addClass("alert-success").show().delay(3000).fadeOut();
    new PNotify({
        title: 'Success',
        text: mes,
        type: 'success',
        styling: 'bootstrap3',
        nonblock: {nonblock: !0},
        before_close: function (e) {
            return e.update({title: e.options.title + " - Enjoy your Stay", before_close: null}), e.queueRemove(), !1
        }
    });
}

function show_warning(mes, fixStatus) {
    // $("#myAlert .alert-content").html(mes);
    // if (isNullOrEmpty(fixStatus) && fixStatus) {
    //     $("#myAlert").removeClass("alert-success alert-danger alert-info").addClass("alert-warning").show();
    // } else {
    //     $("#myAlert").removeClass("alert-success alert-danger alert-info").addClass("alert-warning").show().delay(3000)
    //         .fadeOut();
    // }
    new PNotify({
        title: 'Notice',
        text: mes,
        styling: 'bootstrap3',
        // nonblock: {nonblock: !0},
        // before_close: function (e) {
        //     return e.update({title: e.options.title + " - Enjoy your Stay", before_close: null}), e.queueRemove(), !1
        // },
        hide: false
    });
}

function show_error(mes) {
    // $("#myAlert .alert-content").html(mes);
    // $("#myAlert").removeClass("alert-success alert-warning alert-info").addClass("alert-danger").show();
    new PNotify({
        title: 'Oh No!',
        text: mes,
        type: 'error',
        hide: false,
        styling: 'bootstrap3'
    });

}

function get(lambdaExpr, defaultValue) {// () => supplier expr
    try {
        return lambdaExpr();
    } catch (e) {
        return isNullOrEmpty(defaultValue) ? '' : defaultValue;
    }
}

function arrayToJsonArray(arr) {
    try {
        return arr.reduce((json, value, key) => {
            json[key] = {id: value};
            return json;
        }, []);
    } catch (e) {
        return [];
    }
}

function isNullOrEmpty(param) {
    if (param == null)
        return true;
    if (param == "null")
        return true;
    if (param == undefined)
        return true;
    if (param == "undefined")
        return true;
    if ((param + "").trim() == "")
        return true;
    return false;
}

function addPeriod(nStr) {
    nStr += '';
    x = nStr.split('.');
    x1 = x[0];
    x2 = x.length > 1 ? '.' + x[1] : '';
    let rgx = /(\d+)(\d{3})/;
    while (rgx.test(x1)) {
        x1 = x1.replace(rgx, '$1' + ',' + '$2');
    }
    return x1 + x2;
}

function separetorPrice(id) {
    $(id).each(function () {
        val = $.trim($(this).html());
        if ($.isNumeric(val)) {
            res = addPeriod(val);
            $(this).html(res);
        }
    });
}


function getTagsLabel(element) {
    let labels = [];
    $.each(element.tagsinput('items'), function (i, item) {
        labels.push(item.label);
    });
    return labels;
}

function getTagsFile(element) {
    let files = [];
    $.each(element.tagsinput('items'), function (i, item) {
        if (item.id !== item.label)
            files.push(item.id);
    });
    return files;
}

function getEmptyTagsLabel(element) {
    let labels = [];
    $.each(element.tagsinput('items'), function (i, item) {
        if (item.id === item.label)
            labels.push(item.label);
    });
    return labels;
}

function persianDigitToEnglish(value) {
    return value.replace(/۰/g, "0").replace(/۱/g, "1").replace(/۲/g, "2").replace(/۳/g, "3").replace(/۴/g, "4").replace(/۵/g, "5").replace(/۶/g, "6").replace(/۷/g, "7").replace(/۸/g, "8").replace(/۹/g, "9");
}

function blockUiOptions() {
    return {
        message: '<i class="fa fa-refresh fa-spin fa-2x"></i><br/>' + resources.processing,
        fadeIn: 1000,
        //timeout: 1000,
        overlayCSS: {
            backgroundColor: '#FFF',
            opacity: 0.8,
            cursor: 'wait'
        },
        css: {
            border: 0,
            padding: 0,
            color: '#333',
            backgroundColor: 'transparent'
        },
        onBlock: function () {
            //alert('Page is now blocked, FadeIn complete.');
        }
    }
}

function datePickerOptions() {
    return {
        Placement: 'bottom',
        EnglishNumber: true,
        Trigger: 'click',
        EnableTimePicker: false,
        DisableBeforeToday: false,
        TargetSelector: '.date input',
        GroupId: '',
        ToDate: false,
        FromDate: false,
        Disabled: false,
        Format: 'yyyy/MM/dd',
        GregorianStartDayIndex: 0,
        IsGregorian: false
    }
}

function validationOptions(rules, messages, callback) {
    return {
        ignore: '.skip',
        rules: rules,
        messages: messages,
        highlight: function (element, errorClass, validClass) {
            $(element).closest("div[class^='col-md-']").addClass("has-error").removeClass("has-success");
        },
        unhighlight: function (element, errorClass, validClass) {
            $(element).closest("div[class^='col-md-']").addClass("has-success").removeClass("has-error");
        },
        errorElement: 'span',
        errorClass: 'help-block',
        errorPlacement: function (error, element) {
            if (element.is(":radio") || element.is(":checkbox")) {
                error.appendTo(element.parent().parent());
            } else if (element.parent('.input-group').length) {
                error.insertAfter(element.closest('.input-group'));
            } else {
                error.insertAfter(element);
            }
        },
        submitHandler: function (form) {
            if (typeof callback === 'function') {
                callback();
            }
        }
    }
}

function tableOptions() {
    return {
        "responsive": true,
        "processing": true,
        "searching": false,
        "sort": true,
        "lengthMenu": [[10, 25, 50, 100, 1000, -1], [10, 25, 50, 100, 1000, resources.all]],
        "createdRow": function (row, data, index) {
            let fn = window["createdRow"];
            if (typeof fn === 'function') {
                fn(row, data, index);
            }
        },
        language: {
            processing: resources.processing + "&nbsp;<i class='fa fa-refresh fa-spin'/>",
            search: resources.search,
            lengthMenu: resources.showing + " _MENU_ " + resources.record,
            info: resources.showing + " _START_ " + resources.to + " _END_ " + resources.of + " _TOTAL_ " + resources.entries,
            infoEmpty: resources.showing + " 0 " + resources.to + " 0 " + resources.of + " 0 " + resources.entries,
            infoFiltered: "(" + resources.filtered + " " + resources.from + " _MAX_ " + resources.total + " " + resources.entries + ")",
            infoPostFix: "",
            loadingRecords: resources.loading + "&nbsp;<i class='fa fa-refresh fa-spin'/>",
            zeroRecords: resources.nothingFound,
            emptyTable: resources.nothingFound,
            paginate: {
                first: resources.first,
                previous: resources.previous,
                next: resources.next,
                last: resources.last
            },
            aria: {
                sortAscending: ": " + resources.ascending,
                sortDescending: ": " + resources.descending
            }
        }
    };
}
function initAjaxTable(selector, columns, url, filterFunction) {
    let opts = tableOptions();
    let customTableOptions_function = window["customTableOptions"];
    if (typeof customTableOptions_function === 'function') {
        opts = customTableOptions_function();
    }
    opts.serverSide = true;
    opts.columns = columns;
    opts.ajax = {
        'type': 'GET',
        'url': url,
        'data': function (data) {
            let requestParam = {
                "page": data.start / data.length,
                "size": data.length,
                "sort": data.columns[data.order[0].column].data + ',' + data.order[0].dir
            };
            let fn = window[filterFunction];
            if (typeof fn === 'function') {
                return jsonToUrlSearchParams(fn()) + '&' + jsonToUrlSearchParams(requestParam);
            }
            return requestParam;
        },
        "dataSrc": function(json) {
            json.recordsTotal = json.totalElements;
            json.recordsFiltered = json.totalElements;
            return json.content;
        },
        'datatype': 'json',
        'error': function (xhr) {
            if(isNullOrEmpty(get(()=>xhr.responseJSON)))
                show_error('ajax answer GET returned error: ' + xhr.responseText);
            else show_error(xhr.responseJSON.error + ' (' + xhr.responseJSON.status + ') <br>' + xhr.responseJSON.message);
        }
    };
    return $(selector).DataTable(opts);
}

function initTable(selector, fields, data) {
    let opts = tableOptions();
    opts.columns = fields;
    opts.data = data;
    opts.searching = true;
    return $(selector).DataTable(opts);
}

function loadHtmlFileToElement(fileName, params, selector, callBack) {
    $.blockUI(blockUiOptions());
    $.get("/get-html-file/" + fileName, {params: JSON.stringify(params)},
        function (data) {
            $.unblockUI();
            if (typeof callBack === 'function') {
                $(selector).html(data).css({'overflow': 'auto'}).promise().done(callBack);
            } else {
                $(selector).html(data).css({'overflow': 'auto'});
            }
        });
}

function showModal(title, callback) {
    $('#myModal .modal-title').html(title);
    if (typeof callback === 'function') {
        $('#myModal').on('shown.bs.modal', function () {
            callback();
            $(this).off('shown.bs.modal');
        }).modal('show');
    } else {
        $('#myModal').modal('show');
    }
}

function goToByScroll(selector) {
    $('html, body').animate({
        scrollTop: $(selector).offset().top
    }, 2000);
}

function isArray(value) {
    return value && typeof value === 'object' && value.constructor === Array;
}

function setSwitchery(selector, checked) {
    let element = $(selector);
    if ($(element).is(':checked') != checked) {
        $(element).trigger("click")
    }
}

// function jsonToUrlSearchParams(obj, prefix = '') {
//     let params = new URLSearchParams();
//
//     function flattenObject(obj, currentPrefix = '') {
//         for (const key in obj) {
//             if (obj.hasOwnProperty(key)) {
//                 const value = obj[key];
//                 const newPrefix = currentPrefix ? `${currentPrefix}.${key}` : key;
//
//                 if (value && typeof value === 'object') {
//                     if (Array.isArray(value)) {
//                         value.forEach((item) => {
//                             flattenObject({ [key]: item }, newPrefix);
//                         });
//                     } else {
//                         flattenObject(value, newPrefix);
//                     }
//                 } else if (!isNullOrEmpty(value)) {
//                     params.append(newPrefix, value);
//                 }
//             }
//         }
//     }
//
//     flattenObject(obj, prefix);
//     return params.toString();
// }

function flattenObject(obj, prefix = '') {
    const result = {};
    for (const key in obj) {
        if (obj.hasOwnProperty(key)) {
            const value = obj[key];
            const newKey = prefix ? `${prefix}.${key}` : key;
            if (value && typeof value === 'object' && !Array.isArray(value)) {
                Object.assign(result, flattenObject(value, newKey));
            } else if (!isNullOrEmpty(value)) {
                // Only include non-null values
                result[newKey] = value;
            }
        }
    }
    return result;
}
function jsonToUrlSearchParams(json) {
    const flattened = flattenObject(json);
    const params = new URLSearchParams();
    for (const key in flattened) {
        if (flattened.hasOwnProperty(key)) {
            params.append(key, flattened[key]);
        }
    }
    return params.toString();
}

function objectToQueryString(obj) {
    return Object.entries(obj)
        .map(([key, value]) => {
            // If the value is an array, treat it as multiple occurrences of the same parameter
            if (Array.isArray(value)) {
                return value.map(item => `${encodeURIComponent(key)}=${encodeURIComponent(item)}`).join('&');
            }
            // For non-array values, use a simple key-value pair
            return `${encodeURIComponent(key)}=${encodeURIComponent(value)}`;
        })
        .filter(part => part !== '')
        .join('&');
}

