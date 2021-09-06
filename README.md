# health

Controllers
1. VideoController:  This is to list all the videos and also to upload the file and create new video by associating it to a Category. One small thing if title is null or empty will still be able to create video
2. CategoryController: To list all the categories available in the database.
3. ThumbnailController: Pass the image as form of a resource.

PS: In VideoService class the thumbnail urls are wrong which can be corrected calling this out here. Also In the domains I have not added the AuditLogs such as created by, created at, modified by and modified at.

Also, to upload the videos securely one way to do will be to convert the video resource to the byte array which then can be encoded in the form of string and can be passed in the sample json something like below:

```java
Base64.getEncoder().encodeToString(resource.toByteArray());
```

And similarly on the server side it can be decoded as follows, and this can directly stored into database.

```java
byte[] output = Base64.getDecoder().decode();
```
