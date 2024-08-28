<p align="center">
<img src="./cdpg.png" width="300">
</p>

## Solution Architecture
The following block diagram shows different components/services used in implementing the DX ACL APD Server.
![Solution Architecture](./acl-apd-solution-architecture.png)
The above setup allows specific high load containers/services to scale with ease. Various services of the server are detailed in the sections below.

### API Server
An API server is an HTTPs Web Server that serves as an API gateway for actors (consumers, providers, DX AAA Server) to interact with the different services provided by the DX ACL APD Server.
These services (as described below) may be database read/write services. <br>
It is also responsible for calling the DX AAA Server (via the authorization service) to authenticate and authorize access to resources based on tokens.

### Database Module
Postgres database is called by specific services like Policy service, Notification service, Auth service. It is also used to store the access-request related to CRUD operations, to store emails, first name and last name of the user requesting the APIs. 

### Auditing Service
The DX Auditing Server is integrated with the DX ACL APD Server using the Data Broker. The DX ACL APD Server through the API Server connects with the Auditing Server using the Data Broker as a message bus to log information related to successful creation, deletion of policies and successful creation, updation, and deletion of access requests.

### Authentication Service
The authentication service interacts with the DX AAA Server to validate tokens provided by a user to fetch information about their role and access restrictions.

### Policy Service
The policy service is used to create, delete or list policies, for the resources owned by the provider. Delegates of the provider could manage policies on behalf of the provider. The policy can contain user specific constraints for a given resource. While creating a policy, the provider or provider delegate, provides the ID of the resource, consumer email ID, constraints like subscription, file, async etc., along with policy expiry time in `yyyy-MM-dd'T'HH:mm:ss` format for consumer to access the resource. By default the policy would expire in 12 days (if the expiry time is not provided).
The provider or delegate of the provider can also withdraw an active policy by providing the policy ID.
After the policy is successfully created, provider, consumer, delegates associated to the policy can view all the information related to it.
DX AAA Server uses this created policy using the verify policy API before issuing a token to the consumer.

### Notification Service
The Notification Service is used by a Consumer or consumer delegates to connect with a Provider. A consumer should provide information of the resource like its ID, purpose of access, purpose of usage (Academia, research, non-commercial) to help the provider take an informed decision while approving the request.
An email will be sent to the provider and provider delegates to approve or reject the request. 
