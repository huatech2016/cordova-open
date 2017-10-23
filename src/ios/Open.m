#import "Open.h"

@implementation Open

/**
 *  open
 *
 *  @param command An array of arguments passed from javascript
 */
- (void)open:(CDVInvokedUrlCommand *)command {


    dispatch_async(dispatch_get_main_queue(), ^{
        // Call UI related operations
   
    CDVPluginResult* commandResult = nil;

    NSArray  *arguments = command.arguments;
    NSString *fileId;
    NSString  *fileName;
    NSString  *extension;
    if (!arguments || [arguments count] < 2) {
        NSLog(@"#### setTagsWithAlias param is less");
        return ;
    }else{
        fileId = arguments[0];
        fileName  = arguments[1];
        extension =  [fileName pathExtension];  //aaa.doc -> doc
    }
    NSString *storeName =[NSString stringWithFormat:@"%@.%@",fileId,extension];
    NSLog(@"####  fileId is %@, fileName is %@",fileId,fileName);
    NSString *tmpDir=[NSHomeDirectory() stringByAppendingPathComponent:@"tmp/files"];
    NString *fileName = [tmpDir stringByAppendingPathComponent:[NSString stringWithFormat:@"/%@",storeName]];
    if (fileName != nil && [fileName length] > 0) {

     NSURL *url = [NSURL fileURLWithPath:fileName];
     NSError *err;

      if (url.isFileURL &&
          [url checkResourceIsReachableAndReturnError:&err] == YES) {

        self.fileUrl = url;

        QLPreviewController *previewCtrl = [[QLPreviewController alloc] init];
        previewCtrl.delegate = self;
        previewCtrl.dataSource = self;
          
        [previewCtrl.navigationItem setRightBarButtonItem:nil];
          
        [self.viewController presentViewController:previewCtrl animated:YES completion:nil];

        NSLog(@"cordova.disusered.open - Success!");
        commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                          messageAsString:@""];

      } else {
        NSLog(@"cordova.disusered.open - Invalid file URL wq   ");
          commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsInt:0];
      }
    } else {
      NSLog(@"cordova.disusered.open - Missing URL argument");
      commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }

    [self.commandDelegate sendPluginResult:commandResult callbackId:command.callbackId];
         });
}

-(void)isFileExist:(CDVInvokedUrlCommand*)command{
    NSArray  *arguments = command.arguments;
    NSString *fileId;
    NSString  *fileName;
    NSString  *extension;
    if (!arguments || [arguments count] < 2) {
        NSLog(@"#### setTagsWithAlias param is less");
        return ;
    }else{
        fileId = arguments[0];
        fileName  = arguments[1];
        extension =  [fileName pathExtension];  //aaa.doc -> doc
    }
     NSString *storeName =[NSString stringWithFormat:@"%@.%@",fileId,extension];
    NSLog(@"####  fileId is %@, fileName is %@",fileId,fileName);
    NSString *tmpDir=[NSHomeDirectory() stringByAppendingPathComponent:@"tmp/files"];
    NSString *storFile = [tmpDir stringByAppendingPathComponent:[NSString stringWithFormat:@"/%@",storeName]];

   NSFileManager *fileManager = [NSFileManager defaultManager];
   BOOL result = [fileManager fileExistsAtPath:storFile];
    CDVPluginResult* pluginResult = nil;

   if (result) {
         pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
     } else {
         pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
     }


       [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

}
#pragma - QLPreviewControllerDataSource Protocol

- (NSInteger)numberOfPreviewItemsInPreviewController:
                 (QLPreviewController *)controller {
  return 1;
}

- (id<QLPreviewItem>)previewController:(QLPreviewController *)controller
                    previewItemAtIndex:(NSInteger)index {
  return self;
}

#pragma - QLPreviewItem Protocol

- (NSURL *)previewItemURL {
  return self.fileUrl;
}

@end

