import { ProtelisFile } from "./File";

export default interface ProtelisLibrary {
  name: string
  files: Array<ProtelisFile>
}
