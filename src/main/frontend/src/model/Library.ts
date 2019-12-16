import { ProtelisFile } from './File';

/** Models a library for Protelis. */
export default interface ProtelisLibrary {
  /** The library name. */
  name: string;
  /** The library content. */
  files: Array<ProtelisFile>;
}
