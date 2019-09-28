export interface Version {
  versionCode: number
  versionName: string
  packageName: string
}

export interface VersionIOS extends Version {
  bundleId: string
}

export interface IVersionManager<T> {

  get(): Promise<T>;

  install(args: InstallArgs);

}

export interface InstallArgs {
  title?: string,
  description?: string,
  downloadUrl: string
}
