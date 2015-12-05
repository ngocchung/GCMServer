# GCM Server
Simple Google Cloud Messaging Server Android Application

```
	jsonBody.put("to", CLIENT_REGISTRATION_TOKEN);
    jsonBody.put("delay_while_idle", true);
    jsonData.put("tickerText", "My Ticket");
    jsonData.put("contentTitle", "My Title");
    jsonData.put("message", "GCM message from GCMServer...");
    jsonBody.put("data", jsonData);
    String message = jsonBody.toString();

    URL url = new URL("https://android.googleapis.com/gcm/send");
    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
    urlConnection.setDoInput(true);
    urlConnection.setDoOutput(true);
    urlConnection.setRequestMethod("POST");
    urlConnection.setRequestProperty("Content-Type", "application/json");
    urlConnection.setRequestProperty("Authorization", "key=" + API_KEY);

    OutputStream outputStream = new BufferedOutputStream(urlConnection.getOutputStream());
    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));    
    writer.write(message);
    writer.flush();
    writer.close();
    outputStream.close();		
```

[GCM Client App](https://github.com/ngocchung/gcmandroid)
