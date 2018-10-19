<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>${successModel.mailTitle}</title>
</head>
<body>
您好！您求助的文献 ${successModel.docTitle} 已应助成功。<br/>
请点击以下链接下载全文 <a href='${successModel.downloadUrl}' target='blank'> ${successModel.downloadUrl} </a><br/>
注意：该链接有效期为15天（  ${expStr} 止），请及时下载。<br/>
<br/>
欢迎您使用${channelName}<br/>
<a href='${channelUrl}' target='blank'>${channelUrl}</a>
</body>
</html>