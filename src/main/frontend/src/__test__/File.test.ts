import {
  ProtelisFile, ProtelisFolder, ProtelisSourceFile,
  renameFileAtPath, removeFileAtPath, getFileAtPath, getSourceFileAtPath, getFolderAtPath,
} from '../model/File';

describe('File utils', () => {
  let dummyFolder: ProtelisFolder = { name: 'folder', content: new Set() };
  let dummyFile: ProtelisSourceFile = { name: 'foo', content: 'bar' };
  let newName = 'bar';

  beforeEach(() => {
    dummyFolder = { name: 'folder', content: new Set() };
    dummyFile = { name: 'foo', content: 'bar' };
    newName = 'bar';
  });

  it('can rename a top level file', () => {
    const set: Set<ProtelisFile> = new Set();
    set.add(dummyFile);
    set.add(dummyFolder);
    const newSet: Set<ProtelisFile> = renameFileAtPath(set, `/${dummyFile.name}`, newName);
    expect(newSet.size).toEqual(set.size);
    expect(Array
      .from(newSet)
      .find((value) => value.name === newName && value.content === newName))
      .toBeDefined();
  });

  it('can rename a top level folder', () => {
    const set: Set<ProtelisFile> = new Set();
    set.add(dummyFile);
    set.add(dummyFolder);
    const newSet: Set<ProtelisFile> = renameFileAtPath(set, `/${dummyFolder.name}`, newName);
    expect(newSet.size).toEqual(set.size);
    expect(Array
      .from(newSet)
      .find((value) => value.name === newName
        && typeof value !== 'string'
        && (value as ProtelisFolder).content.size === dummyFolder.content.size))
      .toBeDefined();
  });

  it('can remove a top level file', () => {
    const set: Set<ProtelisFile> = new Set();
    set.add(dummyFile);
    set.add(dummyFolder);
    const newSet: Set<ProtelisFile> = removeFileAtPath(set, `/${dummyFile.name}`);
    expect(Array
      .from(newSet)
      .find((value) => value.name === dummyFile.name))
      .toBeUndefined();
  });

  it('can remove a top level folder', () => {
    const set: Set<ProtelisFile> = new Set();
    set.add(dummyFile);
    set.add(dummyFolder);
    const newSet: Set<ProtelisFile> = removeFileAtPath(set, `/${dummyFolder.name}`);
    expect(Array
      .from(newSet)
      .find((value) => value.name === dummyFolder.name))
      .toBeUndefined();
  });

  it('can rename a nested file', () => {
    const folderSet: Set<ProtelisFile> = new Set();
    folderSet.add(dummyFile);
    folderSet.add(dummyFolder);
    const folderName = 'Folder2';
    const folder: ProtelisFolder = { name: folderName, content: folderSet };
    const set: Set<ProtelisFile> = new Set();
    set.add(dummyFile);
    set.add(folder);
    const newSet: Set<ProtelisFile> = renameFileAtPath(set, `/${folderName}/${dummyFile.name}`, newName);
    expect(newSet.size).toEqual(set.size);
    const newFolder: ProtelisFolder | undefined = Array
      .from(newSet)
      .find((value) => value.name === folderName && typeof value !== 'string') as ProtelisFolder | undefined;
    expect(newFolder).toBeDefined();
    expect(Array
      .from((newFolder as ProtelisFolder).content)
      .find((value) => value.name === newName))
      .toBeDefined();
  });

  it('can rename a nested folder', () => {
    const folderSet: Set<ProtelisFile> = new Set();
    folderSet.add(dummyFile);
    folderSet.add(dummyFolder);
    const folderName = 'Folder2';
    const folder: ProtelisFolder = { name: folderName, content: folderSet };
    const set: Set<ProtelisFile> = new Set();
    set.add(dummyFile);
    set.add(folder);
    const newSet: Set<ProtelisFile> = renameFileAtPath(set, `/${folderName}/${dummyFolder.name}`, newName);
    expect(newSet.size).toEqual(set.size);
    const newFolder: ProtelisFolder | undefined = Array
      .from(newSet)
      .find((value) => value.name === folderName && typeof value !== 'string') as ProtelisFolder | undefined;
    expect(newFolder).toBeDefined();
    expect(Array
      .from((newFolder as ProtelisFolder).content)
      .find((value) => value.name === newName))
      .toBeDefined();
  });

  it('can remove a nested file', () => {
    const folderSet: Set<ProtelisFile> = new Set();
    folderSet.add(dummyFile);
    folderSet.add(dummyFolder);
    const folderName = 'Folder2';
    const folder: ProtelisFolder = { name: folderName, content: folderSet };
    const set: Set<ProtelisFile> = new Set();
    set.add(dummyFile);
    set.add(folder);
    const newSet: Set<ProtelisFile> = removeFileAtPath(set, `/${folderName}/${dummyFile.name}`);
    expect(newSet.size).toEqual(set.size);
    const newFolder: ProtelisFolder | undefined = Array
      .from(newSet)
      .find((value) => value.name === folderName && typeof value !== 'string') as ProtelisFolder | undefined;
    expect(newFolder).toBeDefined();
    expect(Array
      .from((newFolder as ProtelisFolder).content)
      .find((value) => value.name === dummyFile.name))
      .toBeUndefined();
  });

  it('can remove a nested folder', () => {
    const folderSet: Set<ProtelisFile> = new Set();
    folderSet.add(dummyFile);
    folderSet.add(dummyFolder);
    const folderName = 'Folder2';
    const folder: ProtelisFolder = { name: folderName, content: folderSet };
    const set: Set<ProtelisFile> = new Set();
    set.add(dummyFile);
    set.add(folder);
    const newSet: Set<ProtelisFile> = removeFileAtPath(set, `/${folderName}/${dummyFolder.name}`);
    expect(newSet.size).toEqual(set.size);
    const newFolder: ProtelisFolder | undefined = Array
      .from(newSet)
      .find((value) => value.name === folderName && typeof value !== 'string') as ProtelisFolder | undefined;
    expect(newFolder).toBeDefined();
    expect(Array
      .from((newFolder as ProtelisFolder).content)
      .find((value) => value.name === dummyFolder.name))
      .toBeUndefined();
  });

  it('can open a top-level file', () => {
    const folderSet: Set<ProtelisFile> = new Set();
    folderSet.add(dummyFolder);
    folderSet.add(dummyFile);
    expect(getSourceFileAtPath(folderSet, dummyFile.name)).toEqual(dummyFile);
  });

  it('can open a top-level folder', () => {
    const folderSet: Set<ProtelisFile> = new Set();
    folderSet.add(dummyFile);
    const folder: ProtelisFolder = { name: dummyFolder.name, content: new Set([dummyFile]) };
    folderSet.add(folder);
    const result = getFolderAtPath(folderSet, folder.name);
    expect(result).not.toEqual(dummyFolder);
    expect(result.name).toEqual(folder.name);
    expect(result.content).toEqual(folder.content);
  });

  it('can open a nested file', () => {
    const folderSet: Set<ProtelisFile> = new Set();
    folderSet.add(dummyFile);
    const folder: ProtelisFolder = { name: dummyFolder.name, content: new Set([dummyFile]) };
    folderSet.add(folder);
    const result = getFileAtPath(folderSet, `/${folder.name}/${dummyFile.name}`);
    expect(result).toEqual(dummyFile);
  });

  it('can open a nested folder', () => {
    const folderSet: Set<ProtelisFile> = new Set();
    folderSet.add(dummyFile);
    const folder: ProtelisFolder = {
      name: dummyFolder.name,
      content: new Set([dummyFile, dummyFolder]),
    };
    folderSet.add(folder);
    const result = getFolderAtPath(folderSet, `/${folder.name}/${dummyFolder.name}`);
    expect(result).toEqual(dummyFolder);
  });

  it('fails in case of invalid name', () => {
    const folderSet: Set<ProtelisFile> = new Set();
    folderSet.add(dummyFile);
    folderSet.add(dummyFolder);
    const folderName = 'Folder2';
    const folder: ProtelisFolder = { name: folderName, content: folderSet };
    const set: Set<ProtelisFile> = new Set();
    set.add(dummyFile);
    set.add(folder);
    const notExistent = `/${dummyFile.name}/${dummyFile.name}`;
    expect(() => removeFileAtPath(set, notExistent)).toThrow();
    expect(() => renameFileAtPath(set, notExistent, newName)).toThrow();
  });
});
