/** Models a folder. */
export type ProtelisFolder = {
  // path: string | null,
  name: string;
  content: Set<ProtelisFile>;
};

/** Models a file. */
export type ProtelisSourceFile = {
  // path: string | null,
  name: string;
  content: string;
};

/** Models a file or a folder. */
export type ProtelisFile = ProtelisFolder | ProtelisSourceFile;

/**
 * The function removes a file/folder at a path (if present) from a Set of files and folders
 * @param fileSet - the set of files and folders that acts as a root
 * @param filePath - the full path of the file to remove
 *
 * @returns the new root Set without the file to be removed
 */
export function removeFileAtPath(fileSet: Set<ProtelisFile>, filePath: string): Set<ProtelisFile> {
  const folders: string[] = filePath.split('/').filter((s) => s.trim() !== '');
  if (folders.length < 1 /* || folders[0].trim.length === 0 */) {
    throw new Error('Invalid file path specified');
  } else if (folders.length === 1) {
    return new Set<ProtelisFile>(Array
      .from(fileSet)
      .filter((file) => file.name !== folders[0]));
  }
  const [folder, ...rest] = folders;
  return new Set<ProtelisFile>(Array
    .from(fileSet)
    .map((file) => {
      if (file.name === folder) {
        if (typeof file.content === 'string') {
          throw new Error('Not a folder');
        } else {
          return { name: file.name, content: removeFileAtPath(file.content, rest.join('/')) };
        }
      } else {
        return file;
      }
    }));
}

/**
 * The function renames a file/folder at a path (if present) from a Set of files and folders
 * @param fileSet - the set of files and folders that acts as a root
 * @param filePath - the full path of the file to rename
 * @param newName - the new name
 *
 * @returns the new root Set without the file to be renamed
 */
export function renameFileAtPath(
  fileSet: Set<ProtelisFile>,
  filePath: string,
  newName: string,
): Set<ProtelisFile> {
  const folders: string[] = filePath.split('/').filter((s) => s.trim() !== '');
  if (folders.length < 1 /* || folders[0].trim.length === 0 */) {
    throw new Error('Invalid file path specified');
  } else if (folders.length === 1) {
    const fileList = Array.from(fileSet).map((file: ProtelisFile) => {
      if (file.name === folders[0]) {
        file.name = newName;
      }
      return file;
    });
    return new Set<ProtelisFile>(fileList);
  }
  const [folder, ...rest] = folders;
  return new Set<ProtelisFile>(Array
    .from(fileSet)
    .map((file) => {
      if (file.name === folder) {
        if (typeof file.content === 'string') {
          throw new Error('Not a folder');
        } else {
          return { name: file.name, content: renameFileAtPath(file.content, rest.join('/'), newName) };
        }
      } else {
        return file;
      }
    }));
}

export default { removeFileAtPath, renameFileAtPath };
