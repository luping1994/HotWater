var wsUri = "ws://183.236.25.192:24200";//ebike.suntrans-cloud.com
var ws = null;
var closeTimer;

$(function () {
    $("#queding")
        .click(function () {
            var name = $("#paramName").val();
            var status = $("#paramStatus").val();
            console.log(name+","+status);
            switchChannel(name, status);

            dismissDialog();
        });
    $("#qvxiao")
        .click(function () {
            dismissDialog();
        });
});

function dismissDialog() {
    $("#alertDialog").hide();

}

function openDialog(d) {
    console.log(d.id + "," + d.name);
    var title = "";
    var sta;
    if (d.status) {
        sta = "0";
        title = "是否关闭" + d.name;
    } else {
        title = "是否开启" + d.name;
        sta = "1";
    }
    $("#paramName").val(d.id);
    $("#paramStatus").val(sta);
    $("#title").text(title);
    // var modalScale = window.innerWidth / $(window).width();
    // $("#alertDialog").css("transform", "scale(" + modalScale + ")");
    // $("#alertDialog").css("margin-left", (window.pageXOffset + ($(window).width() - 240) * 0.5 * modalScale) + "px");
    // $("#alertDialog").css("margin-top", (window.pageYOffset + 20) + "px");
    $("#alertDialog").show();
}

function webSocket() {
    ws = new WebSocket(wsUri);
    ws.onopen = function () {
        console.log("open");

    };

    ws.onmessage = function (evt) {

        console.log(evt.data);
        // parseMessage(evt.data);
    };

    ws.onclose = function (evt) {

        console.log("webSocketClosed");

    };

    ws.onerror = function (evt) {

        console.log("webSocketError");

    };
}

function switchChannel(name, parameter) {
    var msg = new Object();
    msg.action = "settings";
    msg.name = name;
    msg.parameter = parameter;
    var stringify = JSON.stringify(msg);
    console.log(stringify);
    if (ws) {
        ws.send(stringify);
    }
}

function parseMessage(json) {
    var action =json.action;
    var name =json.name;
    var message =json.message=="0"?false:true;

    switch (action){
        case "feedback":
            for(var i=0;i<datas.elements.length;i++){
                if (datas.elements[i].id == name){
                    datas.elements[i].status = message;
                }
            }
            break;
    }
    $("svg.designer").empty();
    datas.elements.map(createElement);
    datas.text.map(createElement);
    datas.signal.map(createElement);
}