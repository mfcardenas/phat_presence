# PHAT Example Monitoring Activity [04]
<b>A user</b> with tremor in both hands <b>and his caregiver.</b>

The caregiver closes the refrigerator door while the patient walks through the house while suffering from an episode of tremors in both hands.
The user is being monitored with sensors in both hands.
<table>
<tr>
    <td>
        <img height="80" width="80" src="https://github.com/mfcardenas/phat_examples/blob/master/img/in_progress.png" title="The example is under construction"/>
    </td>
    <td>  
To run the demo

```
mvn clean compile
mvn exec:java -Dexec.mainClass=phat.ActvityMonitoringDemo
```
In case of running into memory problems
```
export MAVEN_OPTS="-Xmx512m -XX:MaxPermSize=128m"
```
And then run the previous command.
Alternatively, you can run the startSim.sh file if you are working on Linux.
```
$>./startSim.sh
```

   </td>
    <td>
        <img src="https://github.com/mfcardenas/phat_example_monitoring_04/blob/master/img/img_older_people_home.png" />
    </td>
</tr>
</table>

Remember that you must have the following tools in your computer:

1. Java 1.7
2. Maven 3.1.1 <i>(or higher)</i>. It is necessary to define the environment variable <i><b>M2_HOME</b></i>.

[Guide 2]: http://grasia.fdi.ucm.es/aide/software/2016/01/22/path.html
- See [Guide 2] to set the environment variables on your computer (for Windows OS).
