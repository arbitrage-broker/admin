$(function () {

    $.getJSON("/api/v1/user/countAll", jsonToUrlSearchParams({"roles": 2}), function (data) {
        $("#totalUsers").text(data);
    });

    $.getJSON("/api/v1/wallet/total-balance", jsonToUrlSearchParams({}), function (data) {
        $("#totalBalance").text(get(() => data, 0));
    });

    $.getJSON("/api/v1/wallet/total-bonus", jsonToUrlSearchParams({}), function (data) {
        $("#totalBonus").text(get(() => data, 0));
    });

    $.getJSON("/api/v1/wallet/total-reward", jsonToUrlSearchParams({}), function (data) {
        $("#totalProfit").text(get(() => data, 0));
    });

    $.getJSON("/api/v1/wallet/total-withdrawal", jsonToUrlSearchParams({}), function (data) {
        $("#totalWithdrawal").text(get(() => data, 0));
    });

    $.getJSON("/api/v1/wallet/total-deposit", jsonToUrlSearchParams({}), function (data) {
        $("#totalDeposit").text(get(() => data, 0));
    });
    //chart
    var chart_plot_01_settings = {
        series: {
            lines: {
                show: false,
                fill: true
            },
            splines: {
                show: true,
                tension: 0.4,
                lineWidth: 1,
                fill: 0.4
            },
            points: {
                radius: 0,
                show: true
            },
            shadowSize: 2
        },
        grid: {
            verticalLines: true,
            hoverable: true,
            clickable: true,
            tickColor: "#d5d5d5",
            borderWidth: 1,
            color: '#fff'
        },
        colors: ["rgba(38, 185, 154, 0.38)", "rgba(3, 88, 106, 0.38)"],
        xaxis: {
            tickColor: "rgba(51, 51, 51, 0.06)",
            mode: "time",
            tickSize: [1, "day"],
            //tickLength: 10,
            axisLabel: "Date",
            axisLabelUseCanvas: true,
            axisLabelFontSizePixels: 12,
            axisLabelFontFamily: 'Verdana, Arial',
            axisLabelPadding: 10
        },
        yaxis: {
            ticks: 8,
            tickColor: "rgba(51, 51, 51, 0.06)",
            axisLabel: "Value",  // Added Y-axis label
            axisLabelUseCanvas: true,
            axisLabelFontSizePixels: 12,
            axisLabelFontFamily: 'Verdana, Arial',
            axisLabelPadding: 5
        },
        tooltip: false,
        legend: {  // Added legend settings
            show: true,
            position: "nw",
            labelFormatter: function(label, series) {
                return '<span style="color: #000;">' + label + '</span>';
            }
        }
    };
    if ($("#chart_01").length) {
        $.getJSON("/api/v1/wallet/get-date-range/" + addDays(new Date(),-7).getTime() +"/"+ new Date().getTime() + "/DEPOSIT", function (depositData) {
            const depositArray = Object.entries(depositData).map(([key, value]) => [key, value]).sort((a, b) => Number(a[0]) - Number(b[0]));
            $.getJSON("/api/v1/wallet/get-date-range/" + addDays(new Date(),-7).getTime() +"/"+ new Date().getTime() + "/WITHDRAWAL", function (withdrawalData) {
                const withdrawalArray = Object.entries(withdrawalData).map(([key, value]) => [key, value]).sort((a, b) => Number(a[0]) - Number(b[0]));
                $.plot($("#chart_01"),[depositArray, withdrawalArray],chart_plot_01_settings);
            });
        });
    }
    if ($("#chart_02").length) {
        $.getJSON("/api/v1/wallet/get-date-range/" + addDays(new Date(),-7).getTime() +"/"+ new Date().getTime() + "/BONUS", function (bonusData) {
            const bonusArray = Object.entries(bonusData).map(([key, value]) => [key, value]).sort((a, b) => Number(a[0]) - Number(b[0]));
            $.getJSON("/api/v1/wallet/get-date-range/" + addDays(new Date(),-7).getTime() +"/"+ new Date().getTime() + "/REWARD", function (rewardData) {
                const rewardArray = Object.entries(rewardData).map(([key, value]) => [key, value]).sort((a, b) => Number(a[0]) - Number(b[0]));
                $.plot($("#chart_02"), [bonusArray,rewardArray], chart_plot_01_settings);
            });
        });
    }
});