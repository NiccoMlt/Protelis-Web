import {
  isFolder,
  ProtelisFile,
  ProtelisFolder,
  ProtelisSourceFile,
} from '../model/File';
import {
  getFileAtPath,
  getFolderAtPath,
  getSourceFileAtPath, removeFileAtPath,
  renameFileAtPath,
} from '../utils/fileUtils';

describe('File utils', () => {
  let dummyFolder: ProtelisFolder = { name: 'folder', content: [] };
  let dummyFile: ProtelisSourceFile = { name: 'foo', content: 'bar' };
  let newName = 'bar';

  beforeEach(() => {
    dummyFolder = { name: 'folder', content: [] };
    dummyFile = { name: 'foo', content: 'bar' };
    newName = 'bar';
  });

  it('can rename a top level file', () => {
    const set: ProtelisFile[] = [];
    set.push(dummyFile);
    set.push(dummyFolder);
    const newSet: ProtelisFile[] = renameFileAtPath(set, `/${dummyFile.name}`, newName);
    expect(newSet.length).toEqual(set.length);
    expect(newSet
      .find((value) => value.name === newName && value.content === newName))
      .toBeDefined();
  });

  it('can rename a top level folder', () => {
    const set: ProtelisFile[] = [];
    set.push(dummyFile);
    set.push(dummyFolder);
    const newSet: ProtelisFile[] = renameFileAtPath(set, `/${dummyFolder.name}`, newName);
    expect(newSet.length).toEqual(set.length);
    expect(newSet
      .find((value) => value.name === newName
        && isFolder(value)
        && (value as ProtelisFolder).content.length === dummyFolder.content.length))
      .toBeDefined();
  });

  it('can remove a top level file', () => {
    const set: ProtelisFile[] = [];
    set.push(dummyFile);
    set.push(dummyFolder);
    const newSet: ProtelisFile[] = removeFileAtPath(set, `/${dummyFile.name}`);
    expect(newSet
      .find((value) => value.name === dummyFile.name))
      .toBeUndefined();
  });

  it('can remove a top level folder', () => {
    const set: ProtelisFile[] = [];
    set.push(dummyFile);
    set.push(dummyFolder);
    const newSet: ProtelisFile[] = removeFileAtPath(set, `/${dummyFolder.name}`);
    expect(newSet
      .find((value) => value.name === dummyFolder.name))
      .toBeUndefined();
  });

  it('can rename a nested file', () => {
    const folderSet: ProtelisFile[] = [];
    folderSet.push(dummyFile);
    folderSet.push(dummyFolder);
    const folderName = 'Folder2';
    const folder: ProtelisFolder = { name: folderName, content: folderSet };
    const set: ProtelisFile[] = [];
    set.push(dummyFile);
    set.push(folder);
    const newSet: ProtelisFile[] = renameFileAtPath(set, `/${folderName}/${dummyFile.name}`, newName);
    expect(newSet.length).toEqual(set.length);
    const newFolder: ProtelisFolder | undefined = newSet
      .find((value) => value.name === folderName && isFolder(value)) as ProtelisFolder | undefined;
    expect(newFolder).toBeDefined();
    expect((newFolder as ProtelisFolder).content
      .find((value) => value.name === newName))
      .toBeDefined();
  });

  it('can rename a nested folder', () => {
    const folderSet: ProtelisFile[] = [];
    folderSet.push(dummyFile);
    folderSet.push(dummyFolder);
    const folderName = 'Folder2';
    const folder: ProtelisFolder = { name: folderName, content: folderSet };
    const set: ProtelisFile[] = [];
    set.push(dummyFile);
    set.push(folder);
    const newSet: ProtelisFile[] = renameFileAtPath(set, `/${folderName}/${dummyFolder.name}`, newName);
    expect(newSet.length).toEqual(set.length);
    const newFolder: ProtelisFolder | undefined = newSet
      .find((value) => value.name === folderName && isFolder(value)) as ProtelisFolder | undefined;
    expect(newFolder).toBeDefined();
    expect((newFolder as ProtelisFolder).content
      .find((value) => value.name === newName))
      .toBeDefined();
  });

  it('can remove a nested file', () => {
    const folderSet: ProtelisFile[] = [];
    folderSet.push(dummyFile);
    folderSet.push(dummyFolder);
    const folderName = 'Folder2';
    const folder: ProtelisFolder = { name: folderName, content: folderSet };
    const set: ProtelisFile[] = [];
    set.push(dummyFile);
    set.push(folder);
    const newSet: ProtelisFile[] = removeFileAtPath(set, `/${folderName}/${dummyFile.name}`);
    expect(newSet.length).toEqual(set.length);
    const newFolder: ProtelisFolder | undefined = newSet
      .find((value) => value.name === folderName && isFolder(value)) as ProtelisFolder | undefined;
    expect(newFolder).toBeDefined();
    expect((newFolder as ProtelisFolder).content
      .find((value) => value.name === dummyFile.name))
      .toBeUndefined();
  });

  it('can remove a nested folder', () => {
    const folderSet: ProtelisFile[] = [];
    folderSet.push(dummyFile);
    folderSet.push(dummyFolder);
    const folderName = 'Folder2';
    const folder: ProtelisFolder = { name: folderName, content: folderSet };
    const set: ProtelisFile[] = [];
    set.push(dummyFile);
    set.push(folder);
    const newSet: ProtelisFile[] = removeFileAtPath(set, `/${folderName}/${dummyFolder.name}`);
    expect(newSet.length).toEqual(set.length);
    const newFolder: ProtelisFolder | undefined = newSet
      .find((value) => value.name === folderName && isFolder(value)) as ProtelisFolder | undefined;
    expect(newFolder).toBeDefined();
    expect((newFolder as ProtelisFolder).content
      .find((value) => value.name === dummyFolder.name))
      .toBeUndefined();
  });

  it('can open a top-level file', () => {
    const folderSet: ProtelisFile[] = [];
    folderSet.push(dummyFolder);
    folderSet.push(dummyFile);
    expect(getSourceFileAtPath(folderSet, dummyFile.name)).toEqual(dummyFile);
  });

  it('can open a top-level folder', () => {
    const folderSet: ProtelisFile[] = [];
    folderSet.push(dummyFile);
    const folder: ProtelisFolder = { name: dummyFolder.name, content: [dummyFile] };
    folderSet.push(folder);
    const result = getFolderAtPath(folderSet, folder.name);
    expect(result).not.toEqual(dummyFolder);
    expect(result.name).toEqual(folder.name);
    expect(result.content).toEqual(folder.content);
  });

  it('can open a nested file', () => {
    const folderSet: ProtelisFile[] = [];
    folderSet.push(dummyFile);
    const folder: ProtelisFolder = { name: dummyFolder.name, content: [dummyFile] };
    folderSet.push(folder);
    const result = getFileAtPath(folderSet, `/${folder.name}/${dummyFile.name}`);
    expect(result).toEqual(dummyFile);
  });

  it('can open a nested folder', () => {
    const folderSet: ProtelisFile[] = [];
    folderSet.push(dummyFile);
    const folder: ProtelisFolder = {
      name: dummyFolder.name,
      content: [dummyFile, dummyFolder],
    };
    folderSet.push(folder);
    const result = getFolderAtPath(folderSet, `/${folder.name}/${dummyFolder.name}`);
    expect(result).toEqual(dummyFolder);
  });

  it('fails in case of invalid name', () => {
    const folderSet: ProtelisFile[] = [];
    folderSet.push(dummyFile);
    folderSet.push(dummyFolder);
    const folderName = 'Folder2';
    const folder: ProtelisFolder = { name: folderName, content: folderSet };
    const set: ProtelisFile[] = [];
    set.push(dummyFile);
    set.push(folder);
    const notExistent = `/${dummyFile.name}/${dummyFile.name}`;
    expect(() => removeFileAtPath(set, notExistent)).toThrow();
    expect(() => renameFileAtPath(set, notExistent, newName)).toThrow();
  });
});
