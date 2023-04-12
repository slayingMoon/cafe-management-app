<h1 align="center">Cafe Management System</h1>
<p align="center">Cafe Management System is an app for creating and processing customer orders and managing employees. It's purpose is to be used internally
by the cafe's employees.</p>

<h2>Built using:</h2>
<p>MySQL, Spring Boot, Angular 11, Java</p>

<hr/>

<h2>Landing Page</h2>

<p>
  <img src="./landing-page.PNG" width="70%" />
</p>

<h2>Sign up</h2>

<p>
  <img src="./signup.PNG" width="70%" />
</p>

<hr/>

<h2>Logged as User</h2>

<p>Dashboard</p>
<p>
  <img src="./user-dashboard.PNG" width="70%" />
</p>

<p>Manage Order</p>
<p>
  <img src="./user-order.PNG" width="70%" />
</p>

<p>View Bill</p>
<p>
  <img src="./user-bills.PNG" width="70%" />
</p>

<hr/>
<hr/>

<h2>Logged as Admin</h2>

<p>Dashboard</p>
<p>
  <img src="./admin-dashboard.PNG" width="70%" />
</p>

<p>Manage Category</p>
<p>
  <img src="./admin-category.PNG" width="70%" />
</p>

<p>Manage Product</p>
<p>
  <img src="./admin-product.PNG" width="70%" />
</p>

<p>Manage Order</p>
<p>
  <img src="./admin-order.PNG" width="70%" />
</p>

<p>Manage User</p>
<p>
  <img src="./admin-user.PNG" width="70%" />
</p>

<hr/>

<h2>Application Summary</h2>

-Security, User Authentication, Authorization and Role Management is handled by Spring Security + JWT <br/>
-Single Page Application, Frontend Framework is Angular <br/>
-Responsive design achieved through Angular Material and SCSS <br/>

<h2>Functionality</h2>
-Users can Register/Login/Logout <br/>
-Registered users may have one of the following roles: User, Admin <br/>
-User roles can be managed from the application by Admins <br/>
-Users and administrators can edit their usernames <br/>
-Users and administrators can change their password <br/>
-Users can reset their password by using the 'Forgot Password' functionality <br/>
-Administrator can edit and enable/disable users <br/>
<br/>
-Users and Admins can create orders and generate receipts <br/>
-Users and Admins can delete orders <br/>
-Users can only view, download and delete receipts for orders handled by them <br/>
-Admin has access to all receipts and can view, download and delete them <br/>
-Admin can edit and delete categories <br/>
-Admin can edit, delete and enable/disable products <br/>

<h2>Emails</h2>
-When admin enables/disables user, all admins get notified by email (same functionality for disable) <br/>
<p>
  <img src="./user-enabled.PNG" width="70%" />
</p>

-When user forgets their password, he receives an email with temporary password with which he can login and change his password <br/>
-A cron job, scheduled to run every 10 minutes notifies admins about inactive users <br/>

<h2>Documents</h2>
-Admins and Users can generate receipt documents in .pdf format <br/>

<h2>Error Handling and Data Validation</h2>
-Displaying appropriate messages to users when an error occurs <br/>
-Displaying appropriate validation errors in case form data is invalid <br/>

<h2>Interceptors</h2>
-Added preHandle and postHandle Interceptors, who serve a reporting purpose and log basic data about requests and responses

<hr/>

<h2>Data Management</h2>
-Spring Data JPA with MySQL

<h2>Backend Dependencies</h2>
-lombok <br/>
-jjwt <br/>
-itextpdf <br/>
-spring-boot-starter-mail <br/>
-mapstruct <br/>
-gson <br/>

<h2>Frontend Dependencies</h2>
-angular material <br/>
-angular flex-layout <br/>
-angular forms <br/>
-file-saver <br/>
-jwt-decode <br/>
-ngx-ui-loader <br/>
-rxjs <br/>
-tslib <br/>

<hr/>

<h2>Unit and Integration tests</h2>
<h3>used:</h3>
-spring-boot-starter-test <br/>
-h2 database <br/>
-spring-security-test <br/>
-mockito-core <br/>
-junit-jupiter-api <br/>
-junit-jupiter-engine <br/>

<hr/>

<h1>Instructions:</h1>
<p>To run the application locally don't forget to edit the application.properties file of the Java Spring Boot Application</p>
<p>Also note, that the used version of Angular is 11, so you may need to downgrade.</p>
<p>If you keep getting errors when running npm install, try deleting the package-lock.json file and run npm install --legacy-peer-deps</p>
