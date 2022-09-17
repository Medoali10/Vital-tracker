import "package:flutter/material.dart";
import "package:flutter/cupertino.dart";
import 'package:flutter/foundation.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'info.dart';
import 'dart:convert';
import 'package:http/http.dart' as http;
import 'style.dart';
import 'package:intl/intl.dart';


ScrollController scrollController = ScrollController();
final _textController = TextEditingController();
List<Map> ev = [],temretry = [] , tempev = [] , tempknow = [];
List<String> names = [];
bool init = true , single = false , sug = true , diag = false;
int retry = 0 , ans = 3;


Future<void> user(String txt) async {
  await firestore.collection(userName!).add({
    'text': txt,
    'from': "user",
    'date': DateTime.now().toIso8601String().toString(),
    'time':DateFormat('kk:mm').format(DateTime.now()),
    'time2':DateFormat('yyyy-MM-dd').format(DateTime.now()),
  });
}

Future<void> uin(String text) async{
  if(text.toLowerCase().startsWith("y") && text.length <= 3)ans = 1;
  else if(text.toLowerCase().startsWith("n") && text.length <=4)ans = 0;
  else ans = 2;
}

Future<void> re(int dex) async{
if(ans == 1){
  ev.add(temretry[dex-1]);
}
else if(ans == 0){
  temretry[dex-1]['choice_id']="absent";
  ev.add(temretry[dex-1]);
}
  else{
  temretry[dex-1]['choice_id']="unknown";
  ev.add(temretry[dex-1]);
}
  temretry.removeAt(dex-1);
  ans = 3;
}

Future<void> NLP(String txt) async {
  var response = await http.post(
    Uri.parse("https://api.infermedica.com/v3/parse"),
    headers:  {
      'Content-Type': 'application/json',
      "App-Id" : "aefa5d35",
      "App-Key" : "46d80c8b505dc1f08c52492e324eace9"
    },
    body: json.encode(
        {
          "concept_types": ["symptom"],
          "sex": gender,
          "age": {"value": age},
          "text": txt
        }    ),

  );
  bool notFound = true;
  Map now = jsonDecode(response.body);
print(now);
if(now['mentions'].isNotEmpty){
  if(now['obvious'] == false ){
    for(int i = 0; i<now['mentions'].length; i++){
       if(diag){
         if(tempknow.any((element) => element.containsValue(now['mentions'][i]['id']))){
           notFound = false;
           retry++;
           temretry.add({"choice_id" : "present","id":now['mentions'][i]['id']});
           names.add(now['mentions'][i]['name']);
         }
       }
       else {
         if(temretry.any((element) => element.containsValue(now['mentions'][i]['id']))){
         continue;
       }
         else {
           retry++;
           if(init)temretry.add({"choice_id" : "present","id":now['mentions'][i]['id'], "source": "initial"});
           else temretry.add({"choice_id" : "present","id":now['mentions'][i]['id']});
           names.add(now['mentions'][i]['name']);
         }
      }}
  }else{
    for(int i = 0; i<now['mentions'].length; i++){
      if(diag){
        if(tempknow.any((element) => element.containsValue(now['mentions'][i]['id']))){
          ev.add({"choice_id" : "present","id":now['mentions'][i]['id']});
        }
        else {
          for(int i = 0; i<tempknow.length; i++){
            ev.add(tempknow[i]);
          }
        }
      }else {
        if(ev.any((element) => element.containsValue(now['mentions'][i]['id']))){
          continue;
        }else {
          if(init)ev.add({"choice_id" : "present","id":now['mentions'][i]['id'], "source": "initial"});
          else ev.add({"choice_id" : "present","id":now['mentions'][i]['id']});

        }
 }}
  }
  if(init) init = false;
  diag = true;
}else {
  if(diag){
notFound =true;}
  else {
    await dR("Please use different words");
  }
}
  Map symptoms = jsonDecode(response.body);
  print(symptoms);
  print(names);
  if(retry>0){
    await dR("Do you mean that you have ${names.last}");
    names.removeLast();
  }else{
    if(diag){
        await diagnosis(ev);
    }
  }
  if(notFound && diag){
    for(int i = 0; i<tempknow.length; i++){
      ev.add(tempknow[i]);
    }
    tempknow = [];
  }
}

Future<void> triage(List evid) async {
  var response =  await http.post(
    Uri.parse("https://api.infermedica.com/v3/triage"),
    headers: {
      'Content-Type': 'application/json',
      "App-Id": "aefa5d35",
      "App-Key": "46d80c8b505dc1f08c52492e324eace9"
    },
    body: json.encode(
        {
          "sex": gender,
          "age": {"value": age},
          "evidence": evid
        }),
  );
  Map now = jsonDecode(response.body);
  print(now);
  if(now['triage_level'] == "consultation"){
    await dR("You may require medical evaluation and may need to schedule an appointment with a doctor. If symptoms get worse, you should see a doctor immediately");
  }
  else if(now['triage_level'] == "self_care"){
  await dR("Your symptoms may not require medical evaluation and they usually resolve on their own. Sometimes they can be treated with self-care methods. if the symptoms get worse or new ones appear , you consult a doctor.");
  }
  else if(now['triage_level'] == "emergency_ambulance"){
    await dR("The reported symptoms are very serious and you may require emergency care. You should call an ambulance right now");
  }
  else if(now['triage_level'] == "emergency"){
    await dR("The reported symptoms appears serious and you should go to an emergency department. If you can't get to the nearest emergency department, you should call an ambulance");
  }
  else if(now['triage_level'] == "consultation_24"){
    await dR("You should see a doctor within 24 hours. If the symptoms suddenly get worse, you should go to the nearest emergency department");
  }
}

Future<void> diagnosis(List evid) async {
  var response =  await http.post(
    Uri.parse("https://api.infermedica.com/v3/diagnosis"),
    headers: {
      'Content-Type': 'application/json',
      "App-Id": "aefa5d35",
      "App-Key": "46d80c8b505dc1f08c52492e324eace9"
    },
    body: json.encode(
        {
          "sex": gender,
          "age": {"value": age},
          "evidence": evid,
          "extras": {
            "disable_groups": true
          }
        }),
  );
  print("ev = $evid");
  Map now = jsonDecode(response.body);
  print(now);
  if(now['should_stop'] == true){
   if(now['conditions'].isNotEmpty) await dR("You suffer from ${now['conditions'][0]['name']}");
    await triage(ev);
  }
  else {
    if(now['question']['type'] == "single"){
      single = true;
      await dR(now['question']['text']);
      temretry.add({"choice_id" : "present","id":now['question']['items'][0]['id']});
    }
    else {
      single = false;
      for(int x = 0; x<now['question']['items'].length; x++){
        print(now['question']['items'][x]['id']);
        tempknow.add({"choice_id" : "unknown","id":now['question']['items'][x]['id']});
      }
      await dR(now['question']['text']);
      await dR("please answer in a full sentence");
    }
  }
}


class Chat extends StatefulWidget {
  String gender;

  String username;
  int age;
  Chat({Key? key, required this.username, required this.age, required this.gender}) : super(key: key);

  @override
  _ChatState createState() => _ChatState();
}

class _ChatState extends State<Chat> with TickerProviderStateMixin {


  

  Future<void> callback(String txt) async {
    if (txt.length > 0) {
      await firestore.collection(widget.username).add({
        'text': txt,
        'from': widget.username,
        'date': DateTime.now().toIso8601String().toString(),
      });

      setState(() {});


    }
  }

  void initState() {
    super.initState();
    ev = [];temretry = []; tempev = [];
    names = [];
     init = true; single = false; sug = true; diag = false;
    retry = 0 ; ans = 3;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Color(0xff5b61b9),
      body: SafeArea(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: <Widget>[
            header(context),
            Flexible(
              child: StreamBuilder<QuerySnapshot>(
                stream: firestore
                    .collection(widget.username)
                    .orderBy('date')
                    .snapshots(),
                builder: (context, snapshot) {
                  if (!snapshot.hasData)
                    return Center(
                      child: CircularProgressIndicator(),
                    );

                  List<DocumentSnapshot> docs = snapshot.data!.docs;

                  List messages = docs
                      .map((doc) => doc['from'] == "DRoid" ? receiver(
                    doc['text'],
                    doc['time'],
                  ): sender(doc['text'],
                    doc['time']))
                      .toList();

                  return Container(
                    padding: EdgeInsets.symmetric(horizontal: 30),
                    width: double.infinity,
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.only(
                          topLeft: Radius.circular(45), topRight: Radius.circular(45)),
                      color: Colors.white,
                    ),
                    child: ListView.builder(
                      controller: scrollController,
                      itemBuilder: (_, int index) => messages[index],
                      itemCount: messages.length,
                      padding: EdgeInsets.all(6.0),
                    ),
                  );
                },
              ),
            ),
 Align(
            alignment: Alignment.bottomCenter,
            child: Container(
              height: 120,
              padding: EdgeInsets.symmetric(vertical: 30, horizontal: 20),
              color: Colors.white,
              child: TextField(
                controller: _textController,
                decoration: InputDecoration(
                  hintText: 'Type your message...',
                  suffixIcon: Padding(
                    padding: const EdgeInsets.all(8.0),
                    child: RawMaterialButton(
                      constraints: BoxConstraints(minWidth: 0),
                      onPressed: () async {
                        if(_textController.text.isNotEmpty){
                          String t = _textController.text;
                          _textController.clear();
                          await user(t);
                          scrollController.animateTo(
                            scrollController.position.maxScrollExtent,
                            curve: Curves.easeOut,
                            duration: const Duration(milliseconds: 200),
                          );
                          if(retry>0){
                            await uin(t);
                            re(retry);
                            retry--;
                            if(retry!=0) {
                              await dR("Do you mean that you have ${names[retry - 1]}");
                              names.removeLast();
                            }
                            else {
                             await diagnosis(ev);
                            }
                          }
                          else {
                            if(single){
                              await uin(t);
                              await re(1);
                              print(ev);
                              await diagnosis(ev);
                            }
                             else {
                              await NLP(t);
                            }
                          }
                          scrollController.animateTo(
                            scrollController.position.maxScrollExtent,
                            curve: Curves.easeOut,
                            duration: const Duration(milliseconds: 200),
                          );
                        }},
                      elevation: 2.0,
                      fillColor: Color(0xff5b618d),
                      child: Icon(Icons.send, size: 24.0, color: Colors.white),
                      padding: EdgeInsets.all(10.0),
                      shape: CircleBorder(),
                    ),
                  ),
                  filled: true,
                  fillColor: Colors.blueGrey[50],
                  labelStyle: TextStyle(fontSize: 12),
                  contentPadding: EdgeInsets.all(20),
                  enabledBorder: OutlineInputBorder(
                    borderSide: BorderSide(color: Colors.blueGrey),
                    borderRadius: BorderRadius.circular(25),
                  ),
                  focusedBorder: OutlineInputBorder(
                    borderSide: BorderSide(color: Colors.blueGrey),
                    borderRadius: BorderRadius.circular(25),
                  ),
                ),
              ),
            ),
          ),
        ],
        ),
      ),
    );
  }

}


Widget header(BuildContext context) {
  return Container(
    padding: EdgeInsets.symmetric(horizontal: 30, vertical: 25),
    child: Row(
          children: [
            GestureDetector(
              onTap: () => Navigator.of(context).pop(),
              child: Icon(
                Icons.arrow_back_ios,
                size: 24,
                color: Colors.white,
              ),
            ),
            SizedBox(width: 50,)
,            Text(
              'conversation',
              style: TextStyle(
                  fontSize: 28,
                  fontWeight: FontWeight.bold,
                  color: Colors.white),
            ),
          ],
        ),

  );
}



Widget sender(String message, String time) {
  return Row(
    mainAxisAlignment:
MainAxisAlignment.end,
    crossAxisAlignment: CrossAxisAlignment.end,
    children: [
          Text(
        '$time',
        style: TextStyle(color: Colors.grey.shade400),
      ),
      Flexible(
        child: Container(
          margin: EdgeInsets.only(left: 10, right: 10, top: 20),
          padding: EdgeInsets.all(20),
          decoration: BoxDecoration(
            color:  Colors.indigo.shade100,
            borderRadius:BorderRadius.only(
              topLeft: Radius.circular(30),
              topRight: Radius.circular(30),
              bottomLeft: Radius.circular(30),
            )
          ),
          child: Text('$message'),
        ),
      ),
      SizedBox(),
    ],
  );
}

Widget receiver(String message, String time) {
  return Row(
    mainAxisAlignment:
    MainAxisAlignment.start,
    crossAxisAlignment: CrossAxisAlignment.end,
    children: [
          Avatar(
        image: 'assets/robot.png',
        size: 50,
      ),
      Flexible(
        child: Container(
          margin: EdgeInsets.only(left: 10, right: 10, top: 20),
          padding: EdgeInsets.all(20),
          decoration: BoxDecoration(
color: Colors.indigo.shade50,
  borderRadius:    BorderRadius.only(
              topLeft: Radius.circular(30),
              topRight: Radius.circular(30),
              bottomRight: Radius.circular(30),
            ),
          ),
          child: Text('$message'),
        ),
      ), Text(
        '$time',
        style: TextStyle(color: Colors.grey.shade400),
      )
    ],
  );
}



