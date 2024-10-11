this VendingDataSQSToGoogleSheet Web API where I created a Lambda function using Spring Cloud and uploaded it into AWS Lambda function and connected it to AWS SQS.
this API is for a Vending machine business where transections from vending machines are getting by AWS SQS with AWS SNS through Nayax API. what this API is doing it is working as pipeline getting messages from SQS
and converting it into Object with all fields and value and write this on google sheet
If you want to use this project you need to give your google sheet id into google sheet service class and path of your googlekeyfile.json for authentication
