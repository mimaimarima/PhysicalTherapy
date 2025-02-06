# **Физикална терапија - Physical therapy**

Андроид апликација за физиотерапевти и нивни пациенти - Android app for physiotherapists and their patients

## Преглед - App Screens
![1](https://github.com/user-attachments/assets/b70a16fc-984f-463c-b0f9-d102d2cc4fcb)
![2](https://github.com/user-attachments/assets/314542a2-f482-486b-a724-df80b923b9a2)
![3](https://github.com/user-attachments/assets/8a7552ac-c084-42e3-81a8-947449c3d136)
![4](https://github.com/user-attachments/assets/697b15b2-eee3-4bab-acff-c8ccee87bffe)

## Карактеристики
Физиотерапевтите се хардкодирани во база. Тие додаваат свои пациенти, со нивна мејл-адреса, па им ги кажуваат информациите за најава. 

По најавување, физиотерапевтите добиваат листа со преглед на пациенти. Имаат опција да додадат пациент, со податоци за автентикација, целосно име, возраст, индекс на телесна маса и дијагноза.
При тоа, во базата за пациент автоматски се додаваат нивото на болка и расположение со вредност 0.

Во листата со пациенти, физиотерапевтите кликаат на пациентот за да добијат детален преглед за него. 
Во активноста за детали за пациент е опцијата за главната намена на апликацијата - додавање на вежби кои се дел од терапијата. Со кликање на копчето за додавање вежба, се добива преглед за избирање временски период во кој секој ден ќе треба пациентот да ја направи вежбата. Потоа се добива дијалог за бирање на вежба од хардкодирани вежби во база. Потоа, се внесуваат информации за траењето на вежбата. Ова е текстуален податок, бидејќи може да се допишат траењето во секунди (за вежба како на пр. Баланс на една нога) или бројот на повторувања што треба да се направат.
На копчето за целосен преглед на планот за вежбање, се отвара активност за календарски ден-по-ден преглед на вежбите на пациентот. 

Со кликање на одреден ден, се гледаат внесените вежби за тој ден. Тие може да се уредат или избришат.

По најавување на пациент, се отвара активност со календарски ден-по-ден преглед на вежбите на пациентот. Тој може да ги маркира вежбите како завршени. На икона за преглед, се отвара нова активност со приказ на слика за вежбата. Пациентот со кликање на копче најдолу во оваа претходната календарска активност може да ажурира ниво на болка и ниво на расположение, каде 1 е ниско ниво и 5 високо. Овие информации потоа му се достапни на физиотерапевтот за да може подобро да пристапи во доделување на терапија. Горе на календарската активност има и мотивациона порака.

## Features
Physiotherapists are hardcoded in database. They add patients, with their email address, and tell them login information after.

After logging in, physiotherapists see a Recycler View with overview of patients. They have the option to add a patient, with authentication data, full name, age, BMI and diagnosis. In doing so, the pain level and mood with a value of 0 are automatically added to the database.

In the patient Recycler View, physiotherapists click on the patient to get a detailed overview for the patient.
In the Patient Details Activity the option for the main purpose of the application is found: adding exercises which are part of the therapy. By clicking on the Add exercise button, a DateRange picker is obtained, in which the patient will need to do the exercise each day. Then a dialog is obtained for selecting an exercise from exercises hardcoded in the database. Then, information about the duration of the exercise is entered. This is string data, since the duration could be in seconds (for an exercise such as Single Leg Balance) or the number of repetitions to be done.

The button View Full Exercise Plan opens an activity for a calendar day-by-day overview of the patient's exercises. By clicking on a specific day, the entered exercises for that day are displayed. They can be edited or deleted.

After a patient logs in, an activity with a calendar day-by-day overview of the patient's exercises opens. He can mark the exercises as completed. On the eye icon, a new activity opens with a picture of the exercise. By clicking on the button at the bottom of the calendar activity, the patient can update his pain and mood levels, where 1 is low and 5 is high. This information is then available to the physiotherapist so that he can better approach the assignment of therapy. On top of the calendar activity, a motivation quote is displayed.


## Technologies Used

Programming Language: Java

Database: Firebase

UI Components: Firebase RecyclerView, Dialogs, MaterialCalendar and DateRangePicker

IDE: Android Studio

### Installation

1. Clone this repository:
   ```bash
   git clone https://github.com/mimaimarima/PhysicalTherapy.git
