<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>${mailTitle}</title>
    <style type="text/css">
        .btn22 {
            width: 160px;
            height: 28px;
            color: #fff;
            background-color: #0F9CD6;
            padding-left: 18px;
            border-radius: 15px;
        }
        input,button {
            font-family:"Arial","Tahoma","微软雅黑","雅黑";
            border:0px;
            vertical-align:middle;
            margin:8px;
            line-height:18px;
            font-size:18px;
        }
    </style>
</head>
<body>
您好！您求助的文献：<b> ${docTitle} </b> 已成功为您找到全文。<br/><br/>

请点击<a href='${downloadUrl}' target='blank'>
    <input type='button' class='btn22' value='下载全文'
           onmouseover="this.style.backgroundPosition='left -42px'"
           onmouseout="this.style.backgroundPosition='left top'">
</a>有效期为15天（ ${expStr} 止），请及时下载。

<br/><br/><br/>
欢迎您使用${channelName}<br/>
<a href='${channelUrl}' target='blank'>${channelUrl}</a>
</body>
</html>