import { NativeModules } from 'react-native';
import { InstallArgs, IVersionManager, Version } from './api';

const {
  RNCVersionManager
} = NativeModules;

/**
 * @author 田尘殇Sean(sean.snow@live.com)
 * @date 2017/5/12
 */
export class VersionManager implements IVersionManager<Version> {

  /**
   * 获取版本PackageInfo
   * @returns {*}
   */
  async get(): Promise<Version> {
    console.log('获取版本信息');
    return await RNCVersionManager.get();
  }

  /**
   * 安装
   * @param args 下载参数
   * @returns {*}
   */
  install(args: InstallArgs) {
    return RNCVersionManager.install(args);
  }

}

export default VersionManager;
