<%@ page isErrorPage="true" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Oops! Something went wrong</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #fafafa;
            color: #333;
            text-align: center;
            padding-top: 10%;
        }
        .container {
            max-width: 600px;
            margin: auto;
        }
        h1 {
            font-size: 2em;
            color: #c0392b;
        }
        p {
            margin-top: 1em;
        }
        a {
            color: #2980b9;
            text-decoration: none;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>Oops! Something went wrong.</h1>
    <p>Status code: <strong><%= request.getAttribute("jakarta.servlet.error.status_code") %></strong></p>
</div>
</body>
</html>
