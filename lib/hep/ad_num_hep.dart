import 'package:flutter_ad_ios_plugins/data/storage_data.dart';
import 'package:flutter_ad_ios_plugins/hep/hep.dart';

class AdNumHep{
  static final AdNumHep _instance = AdNumHep();
  static AdNumHep get instance => _instance;

  var _maxShow=100,_maxClick=100,_todayShow=0,_todayClick=0;
  final StorageData<String> _showNumStorage = StorageData(key: "flutter_ios_ad_show_num", defaultValue: "");
  final StorageData<String> _clickNumStorage = StorageData(key: "flutter_ios_ad_click_num", defaultValue: "");

  setMaxNum(int maxShow, int maxClick){
    _maxShow=maxShow;
    _maxClick=maxClick;
    _todayShow=_showNumStorage.getData().getTodayNum();
    _todayClick=_clickNumStorage.getData().getTodayNum();
  }

  notLoad()=> _todayShow>=_maxShow||_todayClick>=_maxClick;

  updateShowNum(){
    _todayShow++;
    _showNumStorage.saveData("${todayTimeStr()}_$_todayShow");
  }

  updateClickNum(){
    _todayClick++;
    _clickNumStorage.saveData("${todayTimeStr()}_$_todayClick");
  }
}