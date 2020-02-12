/** Models a folder. */
export type ProtelisFolder = {
  /** The folder name. */
  name: string;
  /** The files contained in the folder. */
  content: ProtelisFile[];
};

/** Models a file. */
export type ProtelisSourceFile = {
  /** The file name. */
  name: string;
  /** The source code contained in the file. */
  content: string;
};

/** Models a file or a folder. */
export type ProtelisFile = ProtelisFolder | ProtelisSourceFile;

/**
 * Check if a file is a folder.
 *
 * @param file - the file to check
 *
 * @returns true if it's a folder, false otherwise
 */
export function isFolder(file: ProtelisFile): file is ProtelisFolder {
  return Array.isArray(file.content);
}

/**
 * Check if a file is a text file.
 *
 * @param file - the file to check
 *
 * @returns true if it's a text file, false otherwise
 */
export function isSourceFile(file: ProtelisFile): file is ProtelisSourceFile {
  return typeof file.content === 'string';
}
