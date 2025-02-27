import 'package:get_storage/get_storage.dart';

final GetStorage _getStorage=GetStorage();

class StorageData<T>{
  String key;
  final T _defaultValue;
  StorageData({
    required this.key,
    required T defaultValue
  }):_defaultValue=defaultValue;

  saveData(T t){
    _getStorage.write(key, t);
  }

  T getData()=>_getStorage.read(key)??_defaultValue;
}