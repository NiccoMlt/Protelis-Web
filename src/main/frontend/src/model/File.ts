/** Models a folder. */
export type ProtelisFolder = {
  // path: string | null,
  name: string;
  content: ProtelisFile[];
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
export function removeFileAtPath(fileSet: ProtelisFile[], filePath: string): ProtelisFile[] {
  const folders: string[] = filePath.split('/').filter((s) => s.trim() !== '');
  if (folders.length < 1 /* || folders[0].trim.length === 0 */) {
    throw new Error('Invalid file path specified');
  }

  if (folders.length === 1) {
    return fileSet.filter((file) => file.name !== folders[0]);
  }

  const [folder, ...rest] = folders;
  return fileSet.map((file) => {
    if (file.name === folder) {
      if (typeof file.content === 'string') {
        throw new Error('Not a folder');
      } else {
        return { name: file.name, content: removeFileAtPath(file.content, rest.join('/')) };
      }
    } else {
      return file;
    }
  });
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
  fileSet: ProtelisFile[],
  filePath: string,
  newName: string,
): ProtelisFile[] {
  const folders: string[] = filePath.split('/').filter((s) => s.trim() !== '');
  if (folders.length < 1 /* || folders[0].trim.length === 0 */) {
    throw new Error('Invalid file path specified');
  }

  if (folders.length === 1) {
    const fileList = fileSet.map((file: ProtelisFile) => {
      if (file.name === folders[0]) {
        file.name = newName;
      }
      return file;
    });
    return fileList;
  }

  const [folder, ...rest] = folders;
  return fileSet.map((file) => {
    if (file.name === folder) {
      if (typeof file.content === 'string') {
        throw new Error('Not a folder');
      } else {
        return { name: file.name, content: renameFileAtPath(file.content, rest.join('/'), newName) };
      }
    } else {
      return file;
    }
  });
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
  fileSet: ProtelisFile[],
  filePath: string,
): ProtelisFile | never {
  const folders: string[] = filePath.split('/').filter((s) => s.trim() !== '');
  if (folders.length < 1 /* || folders[0].trim.length === 0 */) {
    throw new Error('Invalid file path specified');
  } else {
    const [folder, ...rest] = folders;
    const file: ProtelisFile | undefined = fileSet.find((f) => f.name === folder);
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

/**
 * Search a source file in the set at a given path.
 *
 * @param fileSet - the set of files and folders that acts as a root
 * @param filePath - the full path of the file to search
 *
 * @returns the source file, if exists and is not a folder
 */
export function getSourceFileAtPath(
  fileSet: ProtelisFile[],
  filePath: string,
): ProtelisSourceFile | never {
  const file = getFileAtPath(fileSet, filePath);
  if (isSourceFile(file)) {
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
  fileSet: ProtelisFile[],
  filePath: string,
): ProtelisFolder {
  const file = getFileAtPath(fileSet, filePath);
  if (isFolder(file)) {
    return file as ProtelisFolder;
  }
  throw new Error('Not a folder');
}

export default {
  removeFileAtPath,
  renameFileAtPath,
  getFileAtPath,
  getSourceFileAtPath,
  getFolderAtPath,
  isSourceFile,
  isFolder,
};