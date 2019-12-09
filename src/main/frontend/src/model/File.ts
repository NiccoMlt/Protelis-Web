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

/**
 * Search a file in the set at a given path.
 *
 * @param fileSet - the set of files and folders that acts as a root
 * @param filePath - the full path of the file to search
 *
 * @returns the file, if any
 */
export function getFileAtPath(
  fileSet: Set<ProtelisFile>,
  filePath: string,
): ProtelisFile | never {
  const folders: string[] = filePath.split('/').filter((s) => s.trim() !== '');
  if (folders.length < 1 /* || folders[0].trim.length === 0 */) {
    throw new Error('Invalid file path specified');
  } else {
    const [folder, ...rest] = folders;
    const file: ProtelisFile | undefined = Array.from(fileSet).find((f) => f.name === folder);
    if (file) {
      if (folders.length > 1 && !(typeof file.content === 'string')) {
        return getFileAtPath((file as ProtelisFolder).content, rest.join('/'));
      } if (folders.length === 1) {
        return file;
      }
    }
    throw new Error('No file found at path specified');
  }
}

/**
 * Search a source file in the set at a given path.
 *
 * @param fileSet - the set of files and folders that acts as a root
 * @param filePath - the full path of the file to search
 *
 * @returns the source file, if exists and is not a folder
 */
export function getSourceFileAtPath(
  fileSet: Set<ProtelisFile>,
  filePath: string,
): ProtelisSourceFile | never {
  const file = getFileAtPath(fileSet, filePath);
  if (typeof file.content === 'string') {
    return file as ProtelisSourceFile;
  }
  throw new Error('Not a source file');
}

/**
 * Search a folder in the set at a given path.
 *
 * @param fileSet - the set of files and folders that acts as a root
 * @param filePath - the full path of the folder to search
 *
 * @returns the folder, if exists and is not a source file
 */
export function getFolderAtPath(
  fileSet: Set<ProtelisFile>,
  filePath: string,
): ProtelisFolder {
  const file = getFileAtPath(fileSet, filePath);
  if (typeof file.content !== 'string') {
    return file as ProtelisFolder;
  }
  throw new Error('Not a folder');
}

export default { removeFileAtPath, renameFileAtPath, getFileAtPath };
