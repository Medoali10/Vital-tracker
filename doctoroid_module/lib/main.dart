import 'dart:ui';
import 'package:flutter/material.dart';
import 'info.dart';
import 'package:firebase_core/firebase_core.dart';
import 'firebase_options.dart';



void main() async {
  runApp(MyApp());}

Future fire() async{
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);
  @override
  Widget build(BuildContext context) {
    return FutureBuilder(
      future: fire(),
      builder: (context, snapshot) {
return MaterialApp(
  debugShowCheckedModeBanner: false,
  title: 'Doctoroid',
  theme: ThemeData(
    primarySwatch: Colors.blue,
    visualDensity: VisualDensity.adaptivePlatformDensity,
  ),
  routes: {
    '/': (context) => Info(),
  },
);
      },
    );
  }
}




