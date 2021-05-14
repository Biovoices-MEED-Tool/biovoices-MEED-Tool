/*!
 * bootstrap-star-rating v4.0.6
 * http://plugins.krajee.com/star-rating
 *
 * Author: Kartik Visweswaran
 * Copyright: 2013 - 2019, Kartik Visweswaran, Krajee.com
 *
 * Licensed under the BSD 3-Clause
 * https://github.com/kartik-v/bootstrap-star-rating/blob/master/LICENSE.md
 */


$(document).on('change','.valuesASelect', function(){
    var control = 0
    var selectedValues = [];
    $(".valuesASelect").each(function() {
        var selectedValue = $(this).children("option:selected").val();
        if(selectedValue !== "Select a value" && selectedValue !== "Select a MML" ) {
            selectedValues.push(selectedValue);
            control = control+1;
        }
    });
    if (control==2) {
        console.log("entro - " + selectedValues + control);

        $.ajax({
            url: window.location + "/admin/getScoresA",
            type: "POST",
            data: {selectedValues: selectedValues},
            traditional: true,
            cache: false,
            timeout: 600000,
            success: function (data) {
                $("#resultsFragment").html(data);
                $("#resultsFragment").fadeIn(3000);
                control=0;
            },
            error: function (data) {
                console.log(data.size);
            }
        });
    }
});

$(document).on('change','.valuesBSelect', function() { var control = 0
    var selectedBValues = [];
    var controlB = 0
    $(".valuesBSelect").each(function() {
        var selectedValue = $(this).children("option:selected").val();
        if(selectedValue !== "Select a parameter" && selectedValue !== "Select an activity" ) {
            selectedBValues.push(selectedValue);
            controlB = controlB+1;
        }
    });
    if (controlB==2) {
        console.log("entro - " + selectedBValues + control);

        $.ajax({
            url: window.location + "/admin/getScoresB",
            type: "POST",
            data: {selectedBValues: selectedBValues},
            traditional: true,
            cache: false,
            timeout: 600000,
            success: function (data) {
                $("#resultsBFragment").html(data);
                $("#resultsBFragment").fadeIn(3000);
                controlB=0;
            },
            error: function (data) {
                console.log(data.size);
            }
        });
    }
});


$(document).delegate(".scoreEditAButton", "click", function (event) {

    console.log($(event.target));
    var values = $(event.target).attr("id");
    var score
    $(".scoreSelectA").each(function () {
        score = $(this).children("option:selected").val();
    });
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: window.location.pathname + "/admin/changeScoreMatrixA",
        data: {values: values, score: score},
        dataType: 'json',
        cache: false,
        timeout: 600000,
        success: function (data) {
            window.location.reload();
        },
        error: function (e) {
        }
    });
});

$(document).delegate(".scoreEditBButton", "click", function (event) {

    console.log($(event.target));
    var values = $(event.target).attr("id");
    var score
    $(".scoreSelectB").each(function () {
        score = $(this).children("option:selected").val();
    });
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: window.location.pathname + "/admin/changeScoreMatrixB",
        data: {values: values, score: score},
        dataType: 'json',
        cache: false,
        timeout: 600000,
        success: function (data) {
            window.location.reload();
        },
        error: function (e) {
        }
    });
});


