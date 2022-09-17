import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'chat.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:intl/intl.dart';
import 'firebase_options.dart';

final firestore = FirebaseFirestore.instance;
Future<void> dR(String txt) async {
  await firestore.collection(userName!).add({
    'text': txt,
    'from': "DRoid",
    'date': DateTime.now().toIso8601String().toString(),
    'time':DateFormat('kk:mm').format(DateTime.now()),
    'time2':DateFormat('yyyy-MM-dd').format(DateTime.now()),
  });
}

class CustomButton extends StatelessWidget {
  final VoidCallback callback;
  final String text;

  CustomButton({Key? key, required this.callback, required this.text}) : super(key: key);
  @override
  Widget build(BuildContext context) {
    return Container(
      height: 50,
      margin: EdgeInsets.symmetric(horizontal: 50),
      decoration: BoxDecoration(
        color: Colors.indigo,
        borderRadius: BorderRadius.circular(10),
      ),
      child: MaterialButton(
          highlightColor: Colors.indigoAccent,
          onPressed: callback,
          minWidth: 200.0,
          height: 45.0,
          child: Text(text,
          style: TextStyle(color: Colors.white),),
        ),
    );
  }
}

String? gender;
String? userName;
int? age;

final _nameController = TextEditingController();
final _ageController = TextEditingController();


class Info extends StatefulWidget {
  const Info({Key? key}) : super(key: key);

  @override
  _InfoState createState() => _InfoState();
}

class _InfoState extends State<Info> {
  void dispose() {
    _ageController.dispose();
    _nameController.dispose();
    super.dispose();
  }
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        width: double.infinity,
        decoration: BoxDecoration(
color: Colors.indigo
        ),
        child: Column(
          children: [
            SizedBox(height: 80,),
            Padding(
              padding: EdgeInsets.all(20),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: <Widget>[
                  SizedBox(height: 10,),
                ],
              ),
            ),
            Expanded(
              child: Container(
                  decoration: BoxDecoration(
                      color: Colors.white,
                      borderRadius: BorderRadius.only(
                        topLeft: Radius.circular(60),
                        topRight: Radius.circular(60),
                      )
                  ),
                  child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      crossAxisAlignment: CrossAxisAlignment.center,
                      children: <Widget>[
                        TextField( controller: _nameController,
                      onChanged: (_) => setState((){}),
                            decoration:  InputDecoration(labelText: "Enter your Username",
                              errorText: _nameController.text.isEmpty ? 'Value Can\'t Be Empty' : null,)),
                        SizedBox(height: 10,),
                        TextField(
                          controller: _ageController,
                          onChanged: (_) => setState((){}),
                          decoration:  InputDecoration(labelText: "Enter your Age",
                            errorText: _ageController.text.isEmpty ? 'Value Can\'t Be Empty' : null,),
                          keyboardType: TextInputType.number,
                          inputFormatters: <TextInputFormatter>[
                            NumericalRangeFormatter(min: 1, max: 120),
                            FilteringTextInputFormatter.digitsOnly
                          ],
                        ),
                        SizedBox(height: 20,),
                        Center(
                          child: Text("Gender",
                            style: TextStyle(
                                fontSize: 20
                            ),),
                        ),
                        RadioListTile(
                          title: Text("Male"),
                          value: "male",
                          groupValue: gender,
                          onChanged: (value) {
                            setState(() {
                              gender = value as String?;
                            });
                          },
                        ),
                        RadioListTile(
                          title: Text("female"),
                          value: "female",
                          groupValue: gender,
                          onChanged: (value) {
                            setState(() {
                              gender = value as String?;
                            });
                          },
                        ),
                        SizedBox(height: 10,),
                        CustomButton(
                          text: "DONE",
                          callback: () {
                            if(_nameController.text.isEmpty ||  _ageController.text.isEmpty){
                              showDialog(
                                context: context,
                                builder: (BuildContext context) {
                                  return AlertDialog(
                                    title: Text("Error"),
                                    content: Text("Values can't be null"),
                                    actions: [
                                      TextButton(
                                        child: Text("OK"),
                                        onPressed: () {  Navigator.of(context).pop();},
                                      ),
                                    ],
                                  );
                                },
                              );
                              return ;
                            }
                            setState(() {
                              userName = _nameController.text;
                              age = int.parse( _ageController.text);
                            });
                           if(_ageController.text.isEmpty || gender == null) { showDialog(
                              context: context,
                              builder: (BuildContext context) {
                                return AlertDialog(
                                  title: Text("Error"),
                                  content: Text("Choose your gender"),
                                  actions: [
                                    TextButton(
                                      child: Text("OK"),
                                      onPressed: () {  Navigator.of(context).pop();},
                                    ),
                                  ],
                                );
                              },
                            ); }else { Navigator.push(
                              context,
                              MaterialPageRoute(
                                builder: (context) => Chat(
                                  username: userName as String,
                                  age: age as int,
                                  gender: gender as String,
                                ),
                              ),
                            );
                            dR("Please describe your symptoms");
                          };
  }
                        )
                      ]),
                ),
            ),
          ],
        ),
      ),
    );
  }
}
class NumericalRangeFormatter extends TextInputFormatter {
  final double min;
  final double max;

  NumericalRangeFormatter({required this.min, required this.max});

  @override
  TextEditingValue formatEditUpdate(
      TextEditingValue oldValue,
      TextEditingValue newValue,
      ) {

    if (newValue.text == '') {
      return newValue;
    } else if (int.parse(newValue.text) < min) {
      return TextEditingValue().copyWith(text: min.toStringAsFixed(2));
    } else {
      return int.parse(newValue.text) > max ? oldValue : newValue;
    }
  }
}