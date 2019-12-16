/** Models a folder. */
export type ProtelisFolder = {
  name: string;
  content: ProtelisFile[];
};

/** Models a file. */
export type ProtelisSourceFile = {
  name: string;
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
export function isFolder(file: ProtelisFile): boolean {
  return Array.isArray(file.content);
}

/**
 * Check if a file is a text file.
 *
 * @param file - the file to check
 *
 * @returns true if it's a text file, false otherwise
 */
export function isSourceFile(file: ProtelisFile): boolean {
  return typeof file.content === 'string';
}
