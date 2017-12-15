if ($("#openType").val() == "alarm") {
    $("#btnAlarm").trigger("click");
}


//创建元素
function createElement(ele) {
    if (ele.type == "controlImage") {
        createControlImage(ele);
    } else if (ele.type == 'text') {
        createText(ele);
    } else if (ele.type == "dataText") {
        createDataText(ele);
    }
}

//创建一张图片
function createControlImage(data) {
    var imageGroup = d3.select("svg")
        .append("g")
        .classed("ele", true)
        .data([data]);
    imageGroup.append("image")
        .classed("control-image", true)
        .attr("x", function (d) {
            return d.x
        })
        .attr("y", function (d) {
            return d.y
        })
        .attr("width", function (d) {
            return d.width
        })
        .attr("height", function (d) {
            return d.height
        })
        .attr("xlink:href", function (d) {
            if (d.status)
                return d.openUrl;
            return d.closeUrl;
        });
        // .attr("id", function (d) {
        //     return d.id;
        // });

    imageGroup.on("click", openDialog);
    // imageGroup.attr("transform", function (d) {
    //     return "rotate(" + d.radius + "," + d.x2 + "," + d.y2 + ")";
    // });



}

/**
 * 创建一个文本
 * @param data
 * @return
 */
function createText(data) {
    var textGroup = d3.select("svg")
        .append("g")
        .classed("ele", true)
        .data([data]);

    textGroup.append("rect")
        .classed("shape-text", true)
        .attr("x", function (d) {
            return d.x
        })
        .attr("y", function (d) {
            return d.y
        })
        .attr("rx", function (d) {
            return d.radius
        })
        .attr("ry", function (d) {
            return d.radius
        })
        .attr("width", function (d) {
            return d.width
        })
        .attr("height", function (d) {
            return d.height
        })
        .style({
            'fill': data.fill,
            'stroke': data.stroke,
            'stroke-width': data.strokeWidth,
            "opacity": data.showBg,
            'stroke-dasharray': data.strokeDasharray
        });

    textGroup.append("text")
        .classed("shape-text", true)
        .attr("x", function (d) {
            return Math.round(d.x + d.width / 2)
        })
        .attr("y", function (d) {
            return Math.round(d.y + d.height / 2)
        })
        .attr("dy", ".3em")
        .attr("font-family", function (d) {
            return d.fontFamily;
        })
        .attr("font-size", function (d) {
            return d.fontSize;
        })
        .attr("font-weight", function (d) {
            return d.fontWeight;
        })
        .attr("fill", function (d) {
            return d.color
        })
        .text(function (d) {
            return d.content;
        });
}

/**
 * 创建一个数显框
 * @param data
 * @return
 */
function createDataText(data) {
    var textGroup = d3.select("svg")
        .append("g")
        .classed("ele", true)
        .data([data]);

    textGroup.append("rect")
        .classed("data-text", true)
        .attr("x", function (d) {
            return d.x
        })
        .attr("y", function (d) {
            return d.y
        })
        .attr("rx", function (d) {
            return d.radius
        })
        .attr("ry", function (d) {
            return d.radius
        })
        .attr("width", function (d) {
            return d.width
        })
        .attr("height", function (d) {
            return d.height
        })
        .style({
            'fill': data.fill,
            'stroke': data.stroke,
            'stroke-width': data.strokeWidth,
            "opacity": data.showBg,
            'stroke-dasharray': data.strokeDasharray
        });

    textGroup.append("text")
        .classed("data-text", true)
        .attr("x", function (d) {
            return Math.round(d.x + d.width / 2)
        })
        .attr("y", function (d) {
            return Math.round(d.y + d.height / 2)
        })
        .attr("dy", ".3em")
        .attr("font-family", function (d) {
            return d.fontFamily;
        })
        .attr("font-size", function (d) {
            return d.fontSize;
        })
        .attr("font-weight", function (d) {
            return d.fontWeight;
        })
        .attr("fill", function (d) {
            return d.color
        })
        .text(function (d) {

            return d.value;
        });



}





