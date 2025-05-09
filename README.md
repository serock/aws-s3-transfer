# aws-s3-transfer
Transfer a file to or from AWS S3 using client-side 256-bit AES encryption.

## Prerequisites
* An organization instance of AWS IAM Identity Center
* A local installation of AWS CLI version 2
* A local configuration for programmatic access using IAM Identity Center
* A local installation of Java 21

## Preparing to use the App
Set the following environment variables:

* `AWS_PROFILE`
* `S3_WRAPPING_KEY_PASSWORD`

Sign in using the AWS CLI:

```
aws sso login
```

Optionally, test if an AWS access portal session is active:

```
aws sts get-caller-identity
```

## Uploading a File

```
java -jar aws-s3-transfer-1.0.0-SNAPSHOT.jar upload <local-file> <bucket> <object-key>
```

## Downloading a File

```
java -jar aws-s3-transfer-1.0.0-SNAPSHOT.jar download <bucket> <object-key> <local-file>
```
