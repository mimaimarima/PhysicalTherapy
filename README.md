# **Физикална терапија - Physical therapy**

Андроид апликација за физиотерапевти и нивни пациенти. / Android app for physiotherapists and their patients

## Карактеристики - Featuers
### Физиотерапевтите се хардкодирани во база. Тие додаваат свои пациенти, со нивна мејл-адреса, па им ги кажуваат информациите за најава. // Physiotherapists are hardcoded in database. They add patients, with their email address, and tell them login information later.
### MK
По најавување, физиотерапевтите добиваат листа со преглед на пациенти. Имаат опција да додадат пациент, со податоци за автентикација, целосно име, возраст, индекс на телесна маса и дијагноза. При тоа, во базата за пациент автоматски се додаваат нивото на болка и расположение со вредност 0.
Во листата со пациенти, физиотерапевтите кликаат на пациентот за да добијат детален преглед за него. 
Во активноста за детали за пациент е опцијата за главната намена на апликацијата - додавање на вежби кои се дел од терапијата. Со кликање на копчето за додавање вежба (Add exercise), се добива преглед за избирање временски период во кој секој ден ќе треба пациентот да ја направи вежбата. Потоа се добива дијалог за бирање на вежба од хардкодирани вежби во база. Потоа, се внесуваат информации за траењето на вежбата. Ова е текстуален податок, бидејќи може да се допишат траењето во секунди (за вежба како на пр. Баланс на една нога) или бројот на повторувања што треба да се направат.
На копчето за целосен преглед на планот за вежбање, се отвара активност за календарски ден-по-ден преглед на вежбите на пациентот. Со кликање на одреден ден, се гледаат внесените вежби за тој ден. Тие може да се уредат или избришат.

По најавување на пациент, се отвара активност со календарски ден-по-ден преглед на вежбите на пациентот. Тој може да ги маркира вежбите како завршени. На икона за преглед, се отвара нова активност со приказ на слика за вежбата. Пациентот со кликање на копче најдолу во оваа претходната календарска активност може да ажурира ниво на болка и ниво на расположение, каде 1 е ниско ниво и 5 високо. Овие информации потоа му се достапни на физиотерапевтот за да може подобро да пристапи во доделување на терапија.

### EN 
After logging in, physiotherapists see a Recycler View with overview of patients. They have the option to add a patient, with authentication data, full name, age, BMI and diagnosis. In doing so, the pain level and mood with a value of 0 are automatically added to the database.
In the patient Recycler View, physiotherapists click on the patient to get a detailed overview for the patient.
In the Patient Details Activity the option for the main purpose of the application is found: adding exercises which are part of the therapy. By clicking on the Add exercise button, an overview is obtained for selecting a date range in which the patient will need to do the exercise each day. Then a dialog is obtained for selecting an exercise from exercises hardcoded in the database. Then, information about the duration of the exercise is entered. This is text data, since the duration could be in seconds (for an exercise such as Single Leg Balance) or the number of repetitions to be done.
The button View Full Exercise Plan opens an activity for a calendar day-by-day overview of the patient's exercises. By clicking on a specific day, the entered exercises for that day are viewed. They can be edited or deleted.

After a patient logs in, an activity with a calendar day-by-day overview of the patient's exercises opens. He can mark the exercises as completed. On the eye icon, a new activity opens with a picture of the exercise. By clicking on the button at the bottom of the calendar activity, the patient can update their pain and mood levels, where 1 is low and 5 is high. This information is then available to the physiotherapist so that he can better approach the assignment of therapy.


## Technologies Used

Programming Language: Java

Database: Firebase

UI Components: Firebase RecyclerView, Dialogs, DateRangePicker, ImageViews

IDE: Android Studio

### Installation

1. Clone this repository:
   ```bash
   git clone https://github.com/mimaimarima/PhysicalTherapy.git


### Screens
