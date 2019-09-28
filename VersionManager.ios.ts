/**
 * @author 田尘殇Sean(sean.snow@live.com)
 * @date 2017/5/12
 */
import { NativeModules } from 'react-native';
import { InstallArgs, IVersionManager, VersionIOS } from './api';

const {
  RNCVersionManager
} = NativeModules;

export class VersionManager implements IVersionManager<VersionIOS> {

  async get(): Promise<VersionIOS> {
    console.log('获取IOS版本信息');
    const res = await RNCVersionManager.get();
    if (res.versionCode) {
      try {
        res.versionCode = Number.parseInt(res.versionCode);
      } catch (e) {
      }
    }
    return res;
  }

  /**
   * 安装
   * @param args 下载参数
   * @returns {*}
   */
  install(args: InstallArgs) {
    console.log('安装app,ios不支持');
  }

}

export default VersionManager;
