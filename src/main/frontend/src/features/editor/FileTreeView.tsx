import React from 'react';
import TreeView, { TreeViewProps } from '@material-ui/lab/TreeView';
import {
  DescriptionRounded, FolderOpenRounded, FolderRounded,
} from '@material-ui/icons';
import { TreeItem } from '@material-ui/lab';
import { withStyles } from '@material-ui/styles';
import { Theme, createStyles } from '@material-ui/core';
import { fade } from '@material-ui/core/styles';
import { TreeItemProps } from '@material-ui/lab/TreeItem';
import { ProtelisFile } from '../../model/File';

/** The FileTree view component gets contained files from props. */
type FileTreeViewProps = TreeViewProps & {
  /** The files to show */
  files: Set<ProtelisFile>
};

const StyledTreeItem = withStyles(
  (theme: Theme) => createStyles({
    iconContainer: {
      '& .close': {
        opacity: 0.3,
      },
    },
    group: {
      marginLeft: 12,
      paddingLeft: 12,
      borderLeft: `1px dashed ${fade(theme.palette.text.primary, 0.4)}`,
    },
  }),
)(
  (props: TreeItemProps) => <TreeItem {...props} />,
);

/**
 * The function generates a TreeItem from files and folders.
 * @param file the file(s) to generate items from
 * @param nodeId the id of the node to draw
 *
 * @return the TreeItem(s)
 */
function fileToItem(
  file: ProtelisFile,
  nodeId: number = 1,
): JSX.Element {
  if (Array.isArray(file.content)) {
    let nid: number = nodeId;
    return (
      <StyledTreeItem nodeId={`${nodeId}`} label={file.name} key={`${file.name}-${nodeId}`}>
        {
          file
            .content
            .map((f: ProtelisFile) => {
              const jsx = fileToItem(f, nid);
              nid += 1;
              return jsx;
            })
        }
      </StyledTreeItem>
    );
  }
  return <StyledTreeItem nodeId={`${nodeId}`} label={file.name} key={`${file.name}-${nodeId}`} />;
}

/**
 * React Function Component that draws a TreeView for a file structure.
 * @param props the files to show
 */
const FileTreeView: React.FC<FileTreeViewProps> = (props: FileTreeViewProps) => {
  const { files, ...treeViewProps } = props;
  return (
    <TreeView
      defaultEndIcon={<DescriptionRounded />}
      defaultCollapseIcon={<FolderOpenRounded />}
      defaultExpandIcon={<FolderRounded />}
      {...treeViewProps}
    >
      {Array.from(files).map((f) => fileToItem(f))}
    </TreeView>
  );
};

export default FileTreeView;
